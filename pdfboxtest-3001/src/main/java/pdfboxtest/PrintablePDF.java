package pdfboxtest;

import java.util.List;
import java.util.function.Consumer;

public interface PrintablePDF {

    String getPDFName();

    List<PDFModelEntry> getModelEntries();

    Float getWidth();

    Float getHeight();

    void pdfFormatAdjustments(Consumer<Object> function);
}
