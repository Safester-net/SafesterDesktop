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
package net.safester.application.icons;

import java.util.Objects;

/**
 * Central inventory of common application icons.
 */
public enum AppIconId {

    ADDRESS_BOOK("book_telephone"),
    ATTACHMENT("paperclip"),
    ATTACHMENT_SAVE("paperclip-save"),
    CLOSE("close"),
    COPY("copy"),
    DELETE("delete"),
    FOLDER("folder"),
    FOLDER_OPEN("folder_open"),
    HELP("question"),
    INBOX("inbox"),
    KEY("key"),
    LOCK("lock"),
    MAIL("mail"),
    MAIL_FORWARD("mail_forward"),
    MAIL_REPLY("mail_reply"),
    MAIL_REPLY_ALL("mail_reply_all"),
    MAIL_WRITE("mail_write"),
    PRINT("printer"),
    REFRESH("refresh"),
    SETTINGS("window_gear"),
    SYSTEM_INFO("speech_balloon_answer"),
    WINDOW_SIZE("window_size");

    private final String baseName;

    AppIconId(String baseName) {
        this.baseName = Objects.requireNonNull(baseName, "baseName cannot be null!");
    }

    public String getBaseName() {
        return baseName;
    }
}
