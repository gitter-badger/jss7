/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.map.service.supplementary;

import java.io.IOException;
import java.util.ArrayList;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPParsingComponentException;
import org.mobicents.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ForwardingFeature;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ForwardingInfo;
import org.mobicents.protocols.ss7.map.api.service.supplementary.SSCode;
import org.mobicents.protocols.ss7.map.primitives.SequenceBase;

/**
*
* @author sergey vetyutnev
*
*/
public class ForwardingInfoImpl extends SequenceBase implements ForwardingInfo {

    private SSCode ssCode;
    private ArrayList<ForwardingFeature> forwardingFeatureList;

    public ForwardingInfoImpl() {
        super("ForwardingInfo");
    }

    public ForwardingInfoImpl(SSCode ssCode, ArrayList<ForwardingFeature> forwardingFeatureList) {
        super("ForwardingInfo");
        this.ssCode = ssCode;
        this.forwardingFeatureList = forwardingFeatureList;
    }

    @Override
    public SSCode getSsCode() {
        return ssCode;
    }

    @Override
    public ArrayList<ForwardingFeature> getForwardingFeatureList() {
        return forwardingFeatureList;
    }

    @Override
    protected void _decode(AsnInputStream asnIS, int length) throws MAPParsingComponentException, IOException, AsnException {

        this.ssCode = null;
        this.forwardingFeatureList = null;

        AsnInputStream ais = asnIS.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {

                switch (tag) {
                case Tag.STRING_OCTET:
                    if (!ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + " ssCode: Parameter is not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.ssCode = new SSCodeImpl();
                    ((SSCodeImpl) this.ssCode).decodeAll(ais);
                    break;

                case Tag.SEQUENCE:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".forwardingFeatureList: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);

                    AsnInputStream ais2 = ais.readSequenceStream();
                    this.forwardingFeatureList = new ArrayList<ForwardingFeature>();
                    while (true) {
                        if (ais2.available() == 0)
                            break;

                        ais2.readTag();

                        ForwardingFeature forwardingFeature = new ForwardingFeatureImpl();
                        ((ForwardingFeatureImpl) forwardingFeature).decodeAll(ais2);
                        this.forwardingFeatureList.add(forwardingFeature);
                    }
                    if (this.forwardingFeatureList.size() < 1 || this.forwardingFeatureList.size() > 13) {
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter forwardingFeatureList size must be from 1 to 13, found: " + this.forwardingFeatureList.size(),
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    }
                    break;

                default:
                    ais.advanceElement();
                    break;
                }
            } else {
                ais.advanceElement();
            }
        }

        if (forwardingFeatureList == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": forwardingFeatureList parameter is mandatory but has not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOs) throws MAPException {
        if (this.forwardingFeatureList == null)
            throw new MAPException("forwardingFeatureList parameter must not be null");

        if (this.ssCode != null)
            ((SSCodeImpl) this.ssCode).encodeAll(asnOs);

        try {
            asnOs.writeTag(Tag.CLASS_UNIVERSAL, false, Tag.SEQUENCE);
            int pos = asnOs.StartContentDefiniteLength();
            for (ForwardingFeature item : this.forwardingFeatureList) {
                ((ForwardingFeatureImpl) item).encodeAll(asnOs);
            }
            asnOs.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ".forwardingFeatureList: " + e.getMessage(), e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.ssCode != null) {
            sb.append("ssCode=");
            sb.append(this.ssCode);
        }

        if (this.forwardingFeatureList != null) {
            sb.append("forwardingFeatureList=[");
            boolean firstItem = true;
            for (ForwardingFeature be : this.forwardingFeatureList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }

        sb.append("]");
        return sb.toString();
    }

}
