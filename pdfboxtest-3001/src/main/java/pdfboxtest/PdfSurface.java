package pdfboxtest;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfSurface {


    private final PDPageContentStream contentStream;
    private final PDDocument document;
    private final PDDocument watermarkDoc;

    private PdfSurface(PDPageContentStream contentStream, PDDocument document, PDDocument watermarkDoc) {
        this.contentStream = contentStream;
        this.document = document;
        this.watermarkDoc = watermarkDoc;
    }

    public static PdfSurface createSurface(PDPage page, PDDocument watermarkDoc) {

        PDDocument document = new PDDocument();

        document.addPage(page);
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
            return new PdfSurface(contentStream, document, watermarkDoc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPage(PDPage page) {
        document.addPage(page);
    }

    public void writeTo(OutputStream fn) {

        try {
            contentStream.stroke();
            contentStream.close();
            document.save(fn);
            if (watermarkDoc != null) {
                watermarkDoc.close();
            }
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PDDocument getDocument() {
        return document;
    }
}
