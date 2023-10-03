package pdfboxtest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class PdfGenerator {

    public static void main(String[] args) throws Exception {
        InputStream resourceAsStream = QRTankDownloadTemplate.class.getResourceAsStream("/img/mispell.png");
        try {
            byte[] qrImage =  IOUtils.toByteArray(resourceAsStream);
            ImageInfo imageInfo = new ImageInfo(155, 240, 300, 300, 0, qrImage);
            generate(new QRTankDownloadTemplate(), new FileOutputStream("out-3001.pdf"), imageInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generate(PrintablePDF printablePDF,  OutputStream out, ImageInfo additionalImage) throws IOException {
        InputStream resourceAsStream = PdfGenerator.class.getResourceAsStream(printablePDF.getPDFName());

        PDDocument a1doc =  Loader.loadPDF(new RandomAccessReadBuffer(resourceAsStream));

        PDAcroForm form = a1doc.getDocumentCatalog().getAcroForm();
        PDResources dr = form.getDefaultResources();

        PDFont font = PDType0Font.load(a1doc, PdfGenerator.class.getResourceAsStream("/fonts/CambriaMath.ttf"), false);
        dr.add(font);
        var fields = form.getFields();

        printablePDF.getModelEntries().stream()
                .filter(entry -> entry.getFieldName() != null && !entry.getFieldName().isEmpty())
                .forEach(entry -> {
                    PDField field = form.getField(entry.getFieldName());
                    if (field == null)
                        field = fields.get(0);
                    if (field instanceof PDTextField) {
                        try {
                            ((PDTextField) field).setActions(null);
                            field.setValue(entry.getValue());
                        } catch (IOException e) {
                            IOUtils.closeQuietly(a1doc);
                            System.out.println("Error generating pdf");
                            e.printStackTrace();
                        }
                    }
                });

        writeImageToPdf(a1doc, additionalImage, fields);

        try {
            PDPageTree a1Pages = a1doc.getDocumentCatalog().getPages();
            Iterator<PDPage> iterator = a1Pages.iterator();

            PDPage page = iterator.next();

            PdfSurface surface = PdfSurface.createSurface(page, null);
            while (iterator.hasNext()) {
                surface.addPage(iterator.next());
            }
            form.flatten(filterPDPushButtonFields(form), false);
            surface.writeTo(out);
        } catch (IOException e) {
            System.out.println("Could not generate the PDF");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(a1doc);
        }
    }

    private static List<PDField> filterPDPushButtonFields(PDAcroForm form) {
        return form.getFields().stream().filter(checkBeingPdPushButton()).collect(Collectors.toList());
    }


    private static Predicate<? super PDField> checkBeingPdPushButton() {
        return f -> !(f instanceof PDPushButton);
    }

    private static void writeImageToPdf(PDDocument doc, ImageInfo image, List<PDField> formFields) throws IOException {
        for (var field : formFields) {
            if (field instanceof PDPushButton) {
                PDPushButton pdPushButton = (PDPushButton) field;
                var widgets = pdPushButton.getWidgets();

                if (widgets != null && widgets.size() > 0) {
                    var annotationWidget = widgets.get(0); // just need one widget

                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, image.getContent(), "");
                    float imageScaleRatio = (float) image.getH() / (float) image.getW();

                    PDRectangle buttonPosition = getFieldArea(pdPushButton);
                    float height = buttonPosition.getHeight();
                    float width = height / imageScaleRatio;
                    float x = buttonPosition.getLowerLeftX();
                    float y = buttonPosition.getLowerLeftY();

                    var pdAppearanceStream = new PDAppearanceStream(doc);
                    pdAppearanceStream.setResources(new PDResources());
                    try (PDPageContentStream pdPageContentStream = new PDPageContentStream(doc, pdAppearanceStream)) {
                        pdPageContentStream.drawImage(pdImage, x, y, width, height);
                    }
                    pdAppearanceStream.setBBox(new PDRectangle(x, y, width, height));

                    var pdAppearanceDictionary = annotationWidget.getAppearance();
                    if (pdAppearanceDictionary == null) {
                        pdAppearanceDictionary = new PDAppearanceDictionary();
                        annotationWidget.setAppearance(pdAppearanceDictionary);
                    }

                    pdAppearanceDictionary.setNormalAppearance(pdAppearanceStream);

                }

            }
        }

    }

    private static PDRectangle getFieldArea(PDField field) {
        var fieldDict = field.getCOSObject();
        var fieldAreaArray = (COSArray) fieldDict.getDictionaryObject(COSName.RECT);
        return new PDRectangle(fieldAreaArray);
    }

    public static class ImageInfo {
        private int x, y, w, h, pageIndex;
        private byte[] content;

        public ImageInfo(int x, int y, int w, int h, int pageIndex, byte[] content) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.pageIndex = pageIndex;
            this.content = content;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getW() {
            return w;
        }

        public int getH() {
            return h;
        }

        public byte[] getContent() {
            return content;
        }
    }

    public static class DoesNothingResourceCache extends DefaultResourceCache {
        @Override
        public void put(COSObject indirect, PDXObject xobject) {
            //override to do nothing
        }
    }
    
}
