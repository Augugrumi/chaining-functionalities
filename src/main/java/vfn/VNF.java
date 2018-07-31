package vfn;

/**
 * Basic interface of a VNF object
 */
public interface VNF {
    /**
     * Function that is applied to the message
     * @param message Message on which the function is applied
     * @return String - Message transformed by the VNF
     */
    String functionality(String message);
}
