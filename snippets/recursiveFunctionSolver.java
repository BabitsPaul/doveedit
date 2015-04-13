abstract class HashFunction{
    public abstract String hash(String in);
}

class SHA256 extends HashFunction{
    public String hash(String in){
        return "h<" + in + ">";
    }
}

String str = "sha256(12345sha256(abc)6789sha256(abc))abcdef";

//map all available functions to their names
HashMap<String , HashFunction> functions = new HashMap<>();
functions.put("sha256" , new SHA256());

//create a list with the functionNames (not necassary, but simplifies a lot)
ArrayList<String> functionNames = new ArrayList<>();
functionNames.addAll(functions.keySet());

boolean functionFound = false;
do{//as long as functioncalls can be found proceed
    int currentIndex = 0;
    functionFound = false;

    //search for all opening brackets in the function
    while((currentIndex = str.indexOf('(' , currentIndex)) != -1){
        int closeBracket = str.indexOf(')' , currentIndex + 1);
        int openBracket = str.indexOf('(' , currentIndex + 1);

        if(closeBracket == -1 && openBracket > closeBracket)
            throw new Exception("Invalid syntax - missing bracket");
            //should be replaced with a more specific exceptiontype
        if(closeBracket < openBracket || openBracket == -1){
            //the next closing bracket is before the next opening bracket -> the string can be used as input
            functionFound = true;

            //search for the functionname matching the one in str at the given position
            final int curInd = currentIndex;
            final String tmpStr = str;
            Stream<String> matches = functionNames.stream().filter(n -> tmpStr.regionMatches(curInd - n.length() , n , 0 , n.length()));
            Object[] tmp = matches.toArray();

            if(tmp.length > 1)
                throw new Exception("multiple matching hashfunctions");
            if(tmp.length == 0)
                throw new Exception("no matching hashfunction");

            String f = (String) tmp[0];
            HashFunction function = functions.get(f);

            String input = str.substring(currentIndex + 1, closeBracket);
            String hash = function.hash(input);

            //replace hashfunction call with hash
            String temp = str.substring(0 , currentIndex - f.length());
            temp += hash;
            temp += str.substring(closeBracket + 1);
            str = temp;

            //continue search at the end of the last replaced function
            currentIndex = currentIndex - f.length() - input.length() + hash.length();
        }else
            //go to the next nested function call
            currentIndex = openBracket - 1;
    }
}while(functionFound);

System.out.println("sha256(12345sha256(abc)6789sha256(abc))abcdef");
System.out.println(str);