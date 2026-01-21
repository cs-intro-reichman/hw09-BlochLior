public class ListOldTests {
    public static void main(String[] args) {
        List listTest = new List();
        String testString = "committee_";
        for (char c: testString.toCharArray()) {
            listTest.update(c);
        }
        System.out.println(listTest);
        System.out.println(listTest.remove('f') + "should be false");
        System.out.println(listTest);
        System.out.println(listTest.remove('_') + "should be true");
        System.out.println(listTest);
    }
}
