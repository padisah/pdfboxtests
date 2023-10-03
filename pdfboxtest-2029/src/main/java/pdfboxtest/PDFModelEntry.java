package pdfboxtest;

public class PDFModelEntry {

    private String fieldName;
    private String value;

    public PDFModelEntry(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
