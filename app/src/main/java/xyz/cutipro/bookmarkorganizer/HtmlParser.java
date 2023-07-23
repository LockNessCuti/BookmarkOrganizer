/*
Copyright 2023 LockNessCuti

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package xyz.cutipro.bookmarkorganizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HtmlParser {

    /*
    List of websites that crash app in current version of parser:
    FIXED --- https://www.stackexchange.com/ Caused by proper formatting lolz.
    FIXED --- https://www.t-mobile.com/ Cause by proper formatting also lolz.
     */

    /*
    List of websites who's titles are not grabbed correctly:
    https://www.youtube.com
     */

    public static String ParseForTitle(String url) throws IOException {
        String stringToReturn = null;
        InputStreamReader isr;
        BufferedReader bufferedReader;

        URL urlToParse = new URL(url);
        URLConnection urlToParseConnection = urlToParse.openConnection();
        isr = new InputStreamReader(urlToParseConnection.getInputStream());
        bufferedReader = new BufferedReader(isr);

        String line;
        while ((line = bufferedReader.readLine()) != null) {

            if (line.contains("<title>")) {

                //Title div open and closing tags are on same line.
                if (!line.endsWith("<title>")) {
//                Used for bug fixing.
//                System.out.println(line);
                    stringToReturn = line.substring(line.indexOf("<title>") + 7, line.indexOf("</title>"));
                    break;
                }

                //Believe is used to remove preceding white space.
                if (line.endsWith("<title>")) {

                    stringToReturn = bufferedReader.readLine();

                    while (stringToReturn.startsWith(" ")) {

                        stringToReturn = stringToReturn.substring(1);

                    }
                    break;
                }

            }


        }
        stringToReturn = checkForEscapedCharacters(stringToReturn);

        GlobalVars.Companion.setParsingTitle(false);
        return stringToReturn;
    }

    private static String checkForEscapedCharacters(String string) {
        ArrayList<EscapedChars> arrayList = new ArrayList<>();
        arrayList.add(new EscapedChars("&#038;", "&amp;", "&"));
        arrayList.add(new EscapedChars("&#8211;", "&ndash;", "–"));
        arrayList.add(new EscapedChars("&#034;", "&quot;", "\""));
        arrayList.add(new EscapedChars("&#039;", null, "'"));

        for (int i = 0; i < arrayList.size(); i++) {

            if (string.contains(arrayList.get(i).getCode())) {

                string = string.replace(arrayList.get(i).getCode(), arrayList.get(i).getCharacter());

            }

            if (arrayList.get(i).getEntityName() != null) {

                if (string.contains(arrayList.get(i).getEntityName())) {

                    string = string.replace(arrayList.get(i).getEntityName(), arrayList.get(i).getCharacter());

                }
            }

        }

        return string;
    }
}
