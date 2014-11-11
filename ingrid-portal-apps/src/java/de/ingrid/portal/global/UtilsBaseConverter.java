/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.global;

public class UtilsBaseConverter {

    private static final String baseDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";  
   
     public static String toBase62( int decimalNumber ) {  
         return fromDecimalToOtherBase( 62, decimalNumber );  
     }  
   
     public static String toBase36( int decimalNumber ) {  
         return fromDecimalToOtherBase( 36, decimalNumber );  
     }  
   
     public static String toBase16( int decimalNumber ) {  
         return fromDecimalToOtherBase( 16, decimalNumber );  
     }  
   
     public static String toBase8( int decimalNumber ) {  
         return fromDecimalToOtherBase( 8, decimalNumber );  
     }  
   
     public static String toBase2( int decimalNumber ) {  
         return fromDecimalToOtherBase( 2, decimalNumber );  
     }  
   
     public static int fromBase62( String base62Number ) {  
         return fromOtherBaseToDecimal( 62, base62Number );  
     }  
   
    public static int fromBase36( String base36Number ) {  
        return fromOtherBaseToDecimal( 36, base36Number );  
     }  
   
     public static int fromBase16( String base16Number ) {  
         return fromOtherBaseToDecimal( 16, base16Number );  
     }  

     public static int fromBase8( String base8Number ) {  
         return fromOtherBaseToDecimal( 8, base8Number );  
     }  

     public static int fromBase2( String base2Number ) {  
         return fromOtherBaseToDecimal( 2, base2Number );  
     }  
   
    private static String fromDecimalToOtherBase ( int base, int decimalNumber ) {  
        String tempVal = decimalNumber == 0 ? "0" : "";  
         int mod = 0;  
   
         while( decimalNumber != 0 ) {  
            mod = decimalNumber % base;  
             tempVal = baseDigits.substring( mod, mod + 1 ) + tempVal;  
            decimalNumber = decimalNumber / base;  
         }  
   
         return tempVal;  
    }  
   
     private static int fromOtherBaseToDecimal( int base, String number ) {  
         int iterator = number.length();  
         int returnValue = 0;  
         int multiplier = 1;  
   
         while( iterator > 0 ) {  
             returnValue = returnValue + ( baseDigits.indexOf( number.substring( iterator - 1, iterator ) ) * multiplier );  
             multiplier = multiplier * base;  
             --iterator;  
         }  
         return returnValue;  
     }  
   
}  