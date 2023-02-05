

class Utils {
    
  //---------------------------------------------------------------------------
  public static void fixNullMap(Map map) {
    if (map == null) {
      map = [:];
    }
  }
}