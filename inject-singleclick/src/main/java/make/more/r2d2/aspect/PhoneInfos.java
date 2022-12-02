package make.more.r2d2.aspect;

/**
 * @author HeZX on 2022/4/12.
 */
public class PhoneInfos {

   public static String mac(){
      return getMacAddr();
   }
   private static String getMacAddr() {
      return "02:00:00:00:00:00";
   }
}
