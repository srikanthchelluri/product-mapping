//Srikanth Chelluri
//July 2015

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.*;

public class autoMap {

   private static final String QUESTION_MARK = "?";
   private static double THRESHOLD;

	//INPUT: TEXT FILE WITH EXCEL SHEET DATA
	//OUTPUT: TEXT FILE WITH MAPPING DATA
   public static void main(String[] args) throws FileNotFoundException, IOException {

   	//Get file name, threshold from user
      Scanner userInput = new Scanner(System.in);

      System.out.print("File name? ");
      String fileName = userInput.nextLine();

      System.out.print("Threshold (decimal between 0 and 1, 0 being most particular)? ");
      THRESHOLD = Double.parseDouble(userInput.nextLine());

      System.out.println();

   	//Input file
      File inputTextFile = new File(fileName + ".txt");

   	//Output file
      String outputTextFileName = "output";
      File outputLogFile = new File(outputTextFileName + ".txt"); //CHANGE FILE NAME
      PrintWriter outputFileWriter = new PrintWriter(new FileWriter(outputLogFile));
   	//BufferedWriter outputFileWriter = new BufferedWriter(new FileWriter(outputLogFile));

   	//Open file
      Scanner fileScanner = new Scanner(inputTextFile);

   	//Make two lists:
   	//	listAllLineItems contains all product-code pairs (explicitly from file)
   	//	listAllCodes contains only unique IB codes
      ArrayList<ProductCodeObject> listAllLineItems = new ArrayList<ProductCodeObject>();
      ArrayList<String> listAllCodes = new ArrayList<String>();

      ProductCodeObject currentProductCodeObject;
      while (fileScanner.hasNextLine()) {
         currentProductCodeObject = new ProductCodeObject(fileScanner.next(), fileScanner.next());
         listAllLineItems.add(currentProductCodeObject);

         if (!listAllCodes.contains(currentProductCodeObject.getIbCode()))
            listAllCodes.add(currentProductCodeObject.getIbCode());
      }

   	//Make mapCodeToProductNumbers, map between IB code (no repeats) and all associated product numbers
   	//IB code - String, product numbers - ArrayList<String>
      HashMap<String, ArrayList<String>> mapCodeToProductNumbers = new HashMap<String, ArrayList<String>>();
      for (int index = 0; index < listAllLineItems.size(); index++) {
         String ibCode = listAllLineItems.get(index).getIbCode();
         String productNumber = listAllLineItems.get(index).getProductNumber();

         ArrayList<String> listProductNumbers;
         if (mapCodeToProductNumbers.containsKey(ibCode)) {
            listProductNumbers = mapCodeToProductNumbers.get(ibCode);
            listProductNumbers.add(productNumber);
         } else {
            listProductNumbers = new ArrayList<String>();
            listProductNumbers.add(productNumber);
            mapCodeToProductNumbers.put(ibCode, listProductNumbers);
         }
      }

   	//Make mapCodeToProductNumberTemplate, map between IB code (no repeats) and product number generic template (with wildcards)
      HashMap<String, ArrayList<String>> mapCodeToProductNumberTemplate = new HashMap<String, ArrayList<String>>();
   	//Add all ibCode keys, null values to mapCodeToProductNumberTemplate map
      for (String ibCodeKey : mapCodeToProductNumbers.keySet())
         mapCodeToProductNumberTemplate.put(ibCodeKey, new ArrayList<String>());

   	//Organize each product number list (ArrayList<String>) mapped to IB code (String) based on length
      for (String ibCodeKey : mapCodeToProductNumbers.keySet()) {

         ArrayList<String> listProductNumbersOrg = mapCodeToProductNumbers.get(ibCodeKey);
         Collections.sort(listProductNumbersOrg, new StringSizeComparator());

      }

   	//Instantiate algorithm object
      DamerauLevenshteinAlgorithm dLalgorithm = new DamerauLevenshteinAlgorithm(1, 1, 1, 1); //use method execute(String, String) to return double

   	//Find pattern(s) for each ibCode, add to mapCodeToProductNumberTemplate map
      //MAPPING ALGORITHM
      for (String ibCodeKey : mapCodeToProductNumbers.keySet()) {

         ArrayList<String> listTempProductNumberTemplate = mapCodeToProductNumberTemplate.get(ibCodeKey);
         ArrayList<String> listTempProductNumbers = mapCodeToProductNumbers.get(ibCodeKey);

         if (listTempProductNumbers.size() == 1) { //ibCode referred to by only 1 product number

            listTempProductNumberTemplate.addAll(listTempProductNumbers);

         } else if (listTempProductNumbers.size() == 2) { //ibCode referred to by only 2 product numbers

            String firstProductNumber = listTempProductNumbers.get(0);
            String lastProductNumber = listTempProductNumbers.get(1);

            double differenceIndex = dLalgorithm.execute(firstProductNumber, lastProductNumber);

            if (differenceIndex >= THRESHOLD) {

               listTempProductNumberTemplate.addAll(listTempProductNumbers);

            } else {

               if (firstProductNumber.length() > lastProductNumber.length()) {

                  char[] charLastProductNumber = lastProductNumber.toCharArray();
                  char[] charFirstProductNumber = new char[lastProductNumber.length()];
                  for (int charIndex = 0; charIndex < lastProductNumber.length(); charIndex++)
                     charFirstProductNumber[charIndex] = firstProductNumber.charAt(charIndex);

                  char[] template = new char[charLastProductNumber.length];

                  for (int charIndex = 0; charIndex < template.length; charIndex++)

                     if (charFirstProductNumber[charIndex] == charLastProductNumber[charIndex])
                        template[charIndex] = charFirstProductNumber[charIndex];
                     else
                        template[charIndex] = QUESTION_MARK.charAt(0);

                  listTempProductNumberTemplate.add(String.valueOf(template));

               } else if (lastProductNumber.length() > firstProductNumber.length()) {

                  char[] charFirstProductNumber = firstProductNumber.toCharArray();
                  char[] charLastProductNumber = new char[firstProductNumber.length()];
                  for (int charIndex = 0; charIndex < firstProductNumber.length(); charIndex++)
                     charLastProductNumber[charIndex] = lastProductNumber.charAt(charIndex);

                  char[] template = new char[charFirstProductNumber.length];

                  for (int charIndex = 0; charIndex < template.length; charIndex++)

                     if (charFirstProductNumber[charIndex] == charLastProductNumber[charIndex])
                        template[charIndex] = charFirstProductNumber[charIndex];
                     else
                        template[charIndex] = QUESTION_MARK.charAt(0);

                  listTempProductNumberTemplate.add(String.valueOf(template));

               } else {

                  char[] charFirstProductNumber = firstProductNumber.toCharArray();
                  char[] charLastProductNumber = lastProductNumber.toCharArray();

                  char[] template = new char[charFirstProductNumber.length];

                  for (int charIndex = 0; charIndex < template.length; charIndex++)

                     if (charFirstProductNumber[charIndex] == charLastProductNumber[charIndex])
                        template[charIndex] = charFirstProductNumber[charIndex];
                     else
                        template[charIndex] = QUESTION_MARK.charAt(0);

                  listTempProductNumberTemplate.add(String.valueOf(template));

               }

            }

         } else { //ibCode referred to by more than 2 product numbers

            String currentProductNumber = listTempProductNumbers.get(0);
            String nextProductNumber = listTempProductNumbers.get(1);
            double differenceIndex = dLalgorithm.execute(currentProductNumber, nextProductNumber);
            int index = 2;

            while (listTempProductNumbers.size() >= index + 1) {

               if (differenceIndex >= THRESHOLD) { //currentProductNumber and nextProductNumber dissimilar

                  listTempProductNumberTemplate.add(currentProductNumber);

                  currentProductNumber = nextProductNumber;

               } else { //currentProductNumber and nextProductNumber similar

                  if (currentProductNumber.length() > nextProductNumber.length()) {

                     char[] charCurrentProductNumber = currentProductNumber.toCharArray();
                     char[] charNextProductNumber = new char[currentProductNumber.length()];
                     for (int charIndex = 0; charIndex < nextProductNumber.length(); charIndex++)
                        charNextProductNumber[charIndex] = nextProductNumber.charAt(charIndex);

                     char[] template = new char[charNextProductNumber.length];

                     for (int charIndex = 0; charIndex < template.length; charIndex++)

                        if (charCurrentProductNumber[charIndex] == charNextProductNumber[charIndex])
                           template[charIndex] = charCurrentProductNumber[charIndex];
                        else
                           template[charIndex] = QUESTION_MARK.charAt(0);

                     if (!listTempProductNumberTemplate.contains(String.valueOf(template)))
                        listTempProductNumberTemplate.add(String.valueOf(template));

                  } else if (nextProductNumber.length() > currentProductNumber.length()) {

                     char[] charNextProductNumber = nextProductNumber.toCharArray();
                     char[] charCurrentProductNumber = new char[nextProductNumber.length()];
                     for (int charIndex = 0; charIndex < currentProductNumber.length(); charIndex++)
                        charCurrentProductNumber[charIndex] = currentProductNumber.charAt(charIndex);

                     char[] template = new char[charCurrentProductNumber.length];

                     for (int charIndex = 0; charIndex < template.length; charIndex++)

                        if (charCurrentProductNumber[charIndex] == charNextProductNumber[index])
                           template[charIndex] = charCurrentProductNumber[charIndex];
                        else
                           template[charIndex] = QUESTION_MARK.charAt(0);

                     if (!listTempProductNumberTemplate.contains(String.valueOf(template)))
                        listTempProductNumberTemplate.add(String.valueOf(template));

                  } else {

                     char[] charCurrentProductNumber = currentProductNumber.toCharArray();
                     char[] charNextProductNumber = nextProductNumber.toCharArray();

                     char[] template = new char[charCurrentProductNumber.length];

                     for (int charIndex = 0; charIndex < template.length; charIndex++)

                        if (charCurrentProductNumber[charIndex] == charNextProductNumber[charIndex])
                           template[charIndex] = charCurrentProductNumber[charIndex];
                        else
                           template[charIndex] = QUESTION_MARK.charAt(0);

                     if (!listTempProductNumberTemplate.contains(String.valueOf(template)))
                        listTempProductNumberTemplate.add(String.valueOf(template));

                     currentProductNumber = String.valueOf(template);

                  }

               }

               nextProductNumber = listTempProductNumbers.get(index);

               differenceIndex = dLalgorithm.execute(currentProductNumber, nextProductNumber);
               index++;

            }

            //Assess and map last two values
            if (differenceIndex >= THRESHOLD) {

               listTempProductNumberTemplate.add(currentProductNumber);
               listTempProductNumberTemplate.add(nextProductNumber);

            } else {

               if (currentProductNumber.length() > nextProductNumber.length()) {

                  char[] charCurrentProductNumber = currentProductNumber.toCharArray();
                  char[] charNextProductNumber = new char[currentProductNumber.length()];
                  for (int charIndex = 0; charIndex < nextProductNumber.length(); charIndex++)
                     charNextProductNumber[charIndex] = nextProductNumber.charAt(charIndex);

               } else if (nextProductNumber.length() > currentProductNumber.length()) {

                  char[] charNextProductNumber = nextProductNumber.toCharArray();
                  char[] charCurrentProductNumber = new char[nextProductNumber.length()];
                  for (int charIndex = 0; charIndex < currentProductNumber.length(); charIndex++)
                     charCurrentProductNumber[charIndex] = currentProductNumber.charAt(charIndex);

               } else {

                  char[] charCurrentProductNumber = currentProductNumber.toCharArray();
                  char[] charNextProductNumber = nextProductNumber.toCharArray();

                  char[] template = new char[charCurrentProductNumber.length];

                  for (int charIndex = 0; charIndex < template.length; charIndex++)

                     if (charCurrentProductNumber[charIndex] == charNextProductNumber[charIndex])
                        template[charIndex] = charCurrentProductNumber[charIndex];
                     else
                        template[charIndex] = QUESTION_MARK.charAt(0);

                  if (!listTempProductNumberTemplate.contains(String.valueOf(template)))
                        listTempProductNumberTemplate.add(String.valueOf(template));

               }

            }

         }

      }

   	//Print mapCodeToProductNumberTemplate data to file
      for (String ibCodeKey : mapCodeToProductNumberTemplate.keySet()) {

         ArrayList<String> listProductNumberTemplate = mapCodeToProductNumberTemplate.get(ibCodeKey);
         HashSet<String> setProductNumberTemplate = new HashSet<String>(listProductNumberTemplate);
         listProductNumberTemplate = new ArrayList<String>(setProductNumberTemplate);

         for (int index = 0; index < listProductNumberTemplate.size(); index++)
            outputFileWriter.println(ibCodeKey + "\t" + listProductNumberTemplate.get(index));

      }

      System.out.println("Product mapping is complete. See file \"" + outputTextFileName + "\" for the raw map.");

      //Put mapping data into Excel sheet

      fileScanner.close();
      outputFileWriter.close();

   }

}
