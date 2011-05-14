/*
 * An Error represents an error produced by parsing or running
 * a yoob language.  They're not really used (yet).
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface Error {
    String      getMessage();
    int         getLineNumber();
    int         getColumn();
}
