/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.application.util;

/**
 *Remove macOS and Word Special chars.
 * 
 * @author Nicolas de Pomereu
 */
public class TextCleanUtil {

    public static String cleanSpecialChars(String buffer) {
        if (buffer.indexOf('\u2013') > -1) buffer = buffer.replace('\u2013', '-');
        if (buffer.indexOf('\u2014') > -1) buffer = buffer.replace('\u2014', '-');
        if (buffer.indexOf('\u2015') > -1) buffer = buffer.replace('\u2015', '-');
        if (buffer.indexOf('\u2017') > -1) buffer = buffer.replace('\u2017', '_');
        if (buffer.indexOf('\u2018') > -1) buffer = buffer.replace('\u2018', '\'');
        if (buffer.indexOf('\u2019') > -1) buffer = buffer.replace('\u2019', '\'');
        if (buffer.indexOf('\u201a') > -1) buffer = buffer.replace('\u201a', ',');
        if (buffer.indexOf('\u201b') > -1) buffer = buffer.replace('\u201b', '\'');
        if (buffer.indexOf('\u201c') > -1) buffer = buffer.replace('\u201c', '\"');
        if (buffer.indexOf('\u201d') > -1) buffer = buffer.replace('\u201d', '\"');
        if (buffer.indexOf('\u201e') > -1) buffer = buffer.replace('\u201e', '\"');
        if (buffer.indexOf('\u2026') > -1) buffer = buffer.replace("\u2026", "...");
        if (buffer.indexOf('\u2032') > -1) buffer = buffer.replace('\u2032', '\'');
        if (buffer.indexOf('\u2033') > -1) buffer = buffer.replace('\u2033', '\"');
        return buffer;

    }
}
