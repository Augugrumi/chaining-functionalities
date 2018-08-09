import enchainer.Enchainer;
import vfn.testvnfs.AddStringVNF;

public class Main {

    public static void main(String args[]) {
        int chainLength = 3;
        String[] chain = new String[chainLength];
        chain[0] = "http://localhost:55561";
        chain[1] = "http://localhost:55562";
        chain[2] = "http://localhost:55564";

        Enchainer e = new Enchainer(55563, chain);
        final AddStringVNF v1 = new AddStringVNF("v1", 55561);
        final AddStringVNF v2 = new AddStringVNF("v2", 55562);


        new Thread(v2::execute).start();
        new Thread(v1::execute).start();

        e.execute();
    }
}