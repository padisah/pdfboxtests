package pdfboxtest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ManifestDownloadTemplate implements PrintablePDF {

    private static final String TEMPLATE_PDF_FILE = "/pdf/Template.pdf";

    private static final String TEMPLATE_NAME = "Template Name";
    private static final String DOCUMENT_TYPE = "Document Type";


    private final List<PDFModelEntry> modelEntries = new ArrayList<>();

    
    public ManifestDownloadTemplate() {
        modelEntries.add(new PDFModelEntry(TEMPLATE_NAME, "template-Name"));
        modelEntries.add(new PDFModelEntry(DOCUMENT_TYPE, "document-type"));
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
