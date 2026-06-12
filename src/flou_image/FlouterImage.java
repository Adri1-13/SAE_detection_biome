package flou_image;

public class FlouterImage {

    private ProcessFlou processFlou;

    public FlouterImage(ProcessFlou processFlou) {
        this.processFlou = processFlou;
    }

    public void flouterImage(String src) {
        processFlou.flouterImage(src);
    }

}
