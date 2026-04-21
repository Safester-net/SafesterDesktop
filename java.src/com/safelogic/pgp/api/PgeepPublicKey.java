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

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;

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

    private static String getAlgorithmName(int algorithm)
    {
        switch (algorithm)
        {
            case PublicKeyAlgorithmTags.RSA_GENERAL:
            case PublicKeyAlgorithmTags.RSA_ENCRYPT:
            case PublicKeyAlgorithmTags.RSA_SIGN:
                return "RSA";

            case PublicKeyAlgorithmTags.DSA:
                return "DSA";

            case PublicKeyAlgorithmTags.ECDSA:
                return "ECDSA";

            case PublicKeyAlgorithmTags.ECDH:
                return "ECDH";

            case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT:
            case PublicKeyAlgorithmTags.ELGAMAL_GENERAL:
                return "ELGAMAL";

            default:
                return "PGP";
        }
    }

    @Override
    public String getAlgorithm()
    {
        return getAlgorithmName(m_pgpKey.getAlgorithm());
    }

    @Override
    public byte[] getEncoded()
    {
        try
        {
            return m_pgpKey.getEncoded();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public String getFormat()
    {
        return "PGP";
    }
}
