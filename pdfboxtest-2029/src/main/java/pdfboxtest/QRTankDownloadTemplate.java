package pdfboxtest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class QRTankDownloadTemplate implements PrintablePDF {
    private static final String TEMPLATE_PDF_FILE = "/pdf/TemplateTank.pdf";
    private static final String SITE_NAME = "Site Name";
    private static final String TANK_NAME = "Tank Name";
    private static final String LSD = "LSD";
    private final List<PDFModelEntry> modelEntries = new ArrayList<>();

    public QRTankDownloadTemplate() {
        modelEntries.add(new PDFModelEntry(SITE_NAME, "site-1-1"));
        modelEntries.add(new PDFModelEntry(TANK_NAME, "tankName-1-1"));
        modelEntries.add(new PDFModelEntry(LSD, "lsd"));
    }

    @Override
    public String getPDFName() {
        return TEMPLATE_PDF_FILE;
    }

    @Override
    public List<PDFModelEntry> getModelEntries() {
        return modelEntries;
    }

    @Override
    public Float getWidth() {
        return 2480f;
    }

    @Override
    public Float getHeight() {
        return 3508f;
    }

    @Override
    public void pdfFormatAdjustments(Consumer<Object> function) {
        //No adjustment
    }

}
