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
package com.safelogic.pgp.api;

import java.security.PublicKey;
import java.util.Objects;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;

/**
 * PGP Public Key Holder
 *
 * Necessary because some provider implementations do not define their PGP Public Key as
 * Java PublicKey
 *
 * @author Nicolas de Pomereu
 */
public class PgeepPublicKey implements PublicKey
{
    private static final String PROVIDER = "BC";

    private final PGPPublicKey m_pgpKey;

    /**
     * Constructor
     */
    public PgeepPublicKey(PGPPublicKey pgpKey)
    {
        this.m_pgpKey = Objects.requireNonNull(pgpKey, "pgpKey cannot be null!");
    }

    /**
     * @return Returns the pgpKey.
     */
    public PGPPublicKey getKey()
    {
        return m_pgpKey;
    }

    private PublicKey toJavaPublicKey() throws PGPException
    {
        return new JcaPGPKeyConverter().setProvider(PROVIDER).getPublicKey(m_pgpKey);
    }

    @Override
    public String getAlgorithm()
    {
        try
        {
            PublicKey k = toJavaPublicKey();
            return (k != null) ? k.getAlgorithm() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public byte[] getEncoded()
    {
        try
        {
            PublicKey k = toJavaPublicKey();
            return (k != null) ? k.getEncoded() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public String getFormat()
    {
        try
        {
            PublicKey k = toJavaPublicKey();
            return (k != null) ? k.getFormat() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
