/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package comp;

public class CompilerOptions {
    
   public CompilerOptions() {
      count = false;
      outputInterface = false;
      extractClass = false;
   }
   
   public void setCount(boolean count) {
      this.count = count;
   }
   public boolean getCount() {
      return count;
   }
   
   public void setOutputInterface( boolean outputInterface ) {
      this.outputInterface = outputInterface;
   }
   
   public boolean getOutputInterface() {
      return outputInterface;
   }
   
   public void setExtractClass( boolean extractClass ) {
      this.extractClass = extractClass;
   }
   
   public boolean getExtractClass() {
      return extractClass;
   }
   
   private boolean count, outputInterface, extractClass;
   
}