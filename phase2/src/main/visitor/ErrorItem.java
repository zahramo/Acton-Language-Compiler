package main.visitor;

public class ErrorItem {
    public Integer line;
    public String error;
    ErrorItem(Integer line, String error) {
        this.line = line;
        this.error = error;
    }

    public Integer getLine(){return line;}

    public String getError() {
        return error;
    }
}
