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
package net.safester.noobs.clientserver;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.safester.clientserver.AutoresponderLocal;
import net.safester.clientserver.AutoresponderLocal2;
import net.safester.clientserver.UserPhotoLocal;

/**
 * @author Alexandre Becquereau
 */
public class GsonUtil {

    public static AutoresponderLocal2 autoresponder2FromGson(String jsonString) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<AutoresponderLocal2>() {
        }.getType();
        AutoresponderLocal2 autoresponder2 = gsonOut.fromJson(jsonString, type);
        return autoresponder2;
    }

    /**
     * Update the local Autoresponder2 local instance from remote value
     *
     * @param autoresponderLocal2
     * @return
     */
    public static String autoresponder2ToGson(AutoresponderLocal2 autoresponderLocal2) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<AutoresponderLocal2>() {
        }.getType();
        String jsonString = gsonOut.toJson(autoresponderLocal2, type);
        return jsonString;
    }

    public static String autoresponderToGson(AutoresponderLocal autoresponder) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<AutoresponderLocal>() {
        }.getType();
        String jsonString = gsonOut.toJson(autoresponder, type);
        return jsonString;
    }

    public static AutoresponderLocal autoresponderFromGson(String jsonString) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<AutoresponderLocal>() {
        }.getType();
        AutoresponderLocal autoresponder = gsonOut.fromJson(jsonString, type);
        return autoresponder;
    }

    public static String userPhotoToGson(UserPhotoLocal userPhotoLocal) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<UserPhotoLocal>() {
        }.getType();
        String jsonString = gsonOut.toJson(userPhotoLocal, type);
        return jsonString;
    }

    public static UserPhotoLocal userPhotoFromGson(String jsonString) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<UserPhotoLocal>() {
        }.getType();
        UserPhotoLocal userPhotoLocal = gsonOut.fromJson(jsonString, type);
        return userPhotoLocal;
    }

    public static String listIntegerToGson(List<Integer> listInteger) {
        Gson gsonOut = new Gson();
        Type listOfInteger = new TypeToken<List<Integer>>() {
        }.getType();
        String jsonString = gsonOut.toJson(listInteger, listOfInteger);
        return jsonString;
    }

    public static List<Integer> gsonToListInteger(String jsonString) throws JsonParseException {
        Gson gsonOut = new Gson();
        Type listOfInteger = new TypeToken<List<Integer>>() {
        }.getType();
        List<Integer> listInteger = gsonOut.fromJson(jsonString, listOfInteger);
        return listInteger;
    }

    public static String listToGson(List<FolderLocal> folderLocals) {
        Gson gsonOut = new Gson();
        Type listOfFolderLocal = new TypeToken<List<FolderLocal>>() {
        }.getType();
        String jsonString = gsonOut.toJson(folderLocals, listOfFolderLocal);
        return jsonString;
    }

    public static List<FolderLocal> gsonToList(String jsonString) throws JsonParseException {
        Gson gsonOut = new Gson();
        Type listOfFolderLocal = new TypeToken<List<FolderLocal>>() {
        }.getType();
        List<FolderLocal> folderLocalList = gsonOut.fromJson(jsonString, listOfFolderLocal);
        return folderLocalList;
    }

    public static String messageLocalToGson(MessageLocal messageLocal) {
        Gson gsonOut = new Gson();
        Type typeOfMessageLocal = new TypeToken<MessageLocal>() {
        }.getType();
        String jsonString = gsonOut.toJson(messageLocal, typeOfMessageLocal);
        return jsonString;
    }

    public static String emailGroupLocalToGson(EmailGroupLocal emailGroupLocal) {
        Gson gsonOut = new Gson();
        Type typeOfEmailGroupLocal = new TypeToken<EmailGroupLocal>() {
        }.getType();
        String jsonString = gsonOut.toJson(emailGroupLocal, typeOfEmailGroupLocal);

        return jsonString;
    }

    public static String listGroupMemberLocalToGson(List<GroupMemberLocal> groupMembersLocal) {
        Gson gsonOut = new Gson();

        Type listOfGroupMembersLocal = new TypeToken<List<GroupMemberLocal>>() {
        }.getType();
//        Type listOfString = new ListOfFolderLocal().getType();
        String jsonString = gsonOut.toJson(groupMembersLocal, listOfGroupMembersLocal);
        return jsonString;
    }

    public static List<GroupMemberLocal> gsonToListGroupMemberLocal(String jsonString) throws JsonParseException {
        Gson gsonOut = new Gson();
        Type listOfGroupMembersLocal = new TypeToken<List<GroupMemberLocal>>() {
        }.getType();

        List<GroupMemberLocal> groupMembersLocal = gsonOut.fromJson(jsonString, listOfGroupMembersLocal);
        return groupMembersLocal;
    }

    public static String MessageIdSetToJson(Set<Integer> messageIdSet) {
        Gson gsonOut = new Gson();
        Type setType = new TypeToken<Set<Integer>>() {
        }.getType();
        String jsonString = gsonOut.toJson(messageIdSet, setType);
        return jsonString;
    }

    public static Set<Integer> jsonToMessageIdSet(String jsonMessageIds) {
        Gson gsonOut = new Gson();
        Type setType = new TypeToken<Set<Integer>>() {
        }.getType();
        Set<Integer> messageIdSet = gsonOut.fromJson(jsonMessageIds, setType);
        return messageIdSet;
    }

    public static Map<Integer, List<AttachmentLocal>> attachmentLocalFromGson(String jsonString) {

        Gson gsonOut = new Gson();
        Type typeAttachmentMap = new TypeToken<Map<Integer, List<AttachmentLocal>>>() {
        }.getType();
        Map<Integer, List<AttachmentLocal>> attachmentLocalMap = gsonOut.fromJson(jsonString, typeAttachmentMap);
        return attachmentLocalMap;

    }

    public static Map<String, Set<Integer>> senderUserNumbersPerHostFromGson(String jsonString) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<Map<String, Set<Integer>>>() {
        }.getType();
        Map<String, Set<Integer>> senderUserNumbersPerHost = gsonOut.fromJson(jsonString, type);
        return senderUserNumbersPerHost;
    }

    public static String senderUserNumbersToGson(Set<Integer> senderUserNumbers) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<Set<Integer>>() {
        }.getType();
        String jsonString = gsonOut.toJson(senderUserNumbers, type);
        return jsonString;
    }

    public static Properties gsonToProperties(String jsonString) {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<Properties>() {
        }.getType();
        Properties properties = gsonOut.fromJson(jsonString, type);
        return properties;
    }

    public static String messageBodyLocalToGson(MessageBodyLocal messageBodyLocal) {
        Gson gsonOut = new Gson();
        Type typeOfMessageLocal = new TypeToken<MessageBodyLocal>() {
        }.getType();
        String jsonString = gsonOut.toJson(messageBodyLocal, typeOfMessageLocal);
        return jsonString;
    }

    public static MessageBodyLocal gsonToMessageBodyLocal(String jsonString) throws JsonParseException {
        Gson gsonOut = new Gson();
        Type typeBodyMessageLocal = new TypeToken<MessageBodyLocal>() {
        }.getType();
        MessageBodyLocal messageLocal = gsonOut.fromJson(jsonString, typeBodyMessageLocal);
        return messageLocal;
    }

    public static String attachFilenamesToGson(List<String> attachFilenames) {
        Gson gsonOut = new Gson();
        Type listOfString = new TypeToken<List<String>>() {
        }.getType();
        String jsonString = gsonOut.toJson(attachFilenames, listOfString);
        return jsonString;
    }

    public static UserSettingsLocal userSettingsLocalfromGson(String jsonString) {
        Gson gsonOut = new Gson();
        Type theType = new TypeToken<UserSettingsLocal>() {
        }.getType();
        UserSettingsLocal userSettingsLocal = gsonOut.fromJson(jsonString, theType);
        return userSettingsLocal;
    }

    public static String toGson(UserSettingsLocal userSettingsLocal) {
        Gson gsonOut = new Gson();
        Type theType = new TypeToken<UserSettingsLocal>() {
        }.getType();
        String jsonString = gsonOut.toJson(userSettingsLocal, theType);
        return jsonString;
    }

}
