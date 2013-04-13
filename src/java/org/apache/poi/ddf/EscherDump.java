/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.ddf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.HexRead;
import org.apache.poi.util.LittleEndian;

/**
 * Used to dump the contents of escher records to a PrintStream.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public final class EscherDump {
    private static final Map<Integer, String> PROP_NAMES = new HashMap<>();
    static {
        PROP_NAMES.put(4, "transform.rotation");
        PROP_NAMES.put(119, "protection.lockrotation");
        PROP_NAMES.put(120, "protection.lockaspectratio");
        PROP_NAMES.put(121, "protection.lockposition");
        PROP_NAMES.put(122, "protection.lockagainstselect");
        PROP_NAMES.put(123, "protection.lockcropping");
        PROP_NAMES.put(124, "protection.lockvertices");
        PROP_NAMES.put(125, "protection.locktext");
        PROP_NAMES.put(126, "protection.lockadjusthandles");
        PROP_NAMES.put(127, "protection.lockagainstgrouping");
        PROP_NAMES.put(128, "text.textid");
        PROP_NAMES.put(129, "text.textleft");
        PROP_NAMES.put(130, "text.texttop");
        PROP_NAMES.put(131, "text.textright");
        PROP_NAMES.put(132, "text.textbottom");
        PROP_NAMES.put(133, "text.wraptext");
        PROP_NAMES.put(134, "text.scaletext");
        PROP_NAMES.put(135, "text.anchortext");
        PROP_NAMES.put(136, "text.textflow");
        PROP_NAMES.put(137, "text.fontrotation");
        PROP_NAMES.put(138, "text.idofnextshape");
        PROP_NAMES.put(139, "text.bidir");
        PROP_NAMES.put(187, "text.singleclickselects");
        PROP_NAMES.put(188, "text.usehostmargins");
        PROP_NAMES.put(189, "text.rotatetextwithshape");
        PROP_NAMES.put(190, "text.sizeshapetofittext");
        PROP_NAMES.put(191, "text.sizetexttofitshape");
        PROP_NAMES.put(192, "geotext.unicode");
        PROP_NAMES.put(193, "geotext.rtftext");
        PROP_NAMES.put(194, "geotext.alignmentoncurve");
        PROP_NAMES.put(195, "geotext.defaultpointsize");
        PROP_NAMES.put(196, "geotext.textspacing");
        PROP_NAMES.put(197, "geotext.fontfamilyname");
        PROP_NAMES.put(240, "geotext.reverseroworder");
        PROP_NAMES.put(241, "geotext.hastexteffect");
        PROP_NAMES.put(242, "geotext.rotatecharacters");
        PROP_NAMES.put(243, "geotext.kerncharacters");
        PROP_NAMES.put(244, "geotext.tightortrack");
        PROP_NAMES.put(245, "geotext.stretchtofitshape");
        PROP_NAMES.put(246, "geotext.charboundingbox");
        PROP_NAMES.put(247, "geotext.scaletextonpath");
        PROP_NAMES.put(248, "geotext.stretchcharheight");
        PROP_NAMES.put(249, "geotext.nomeasurealongpath");
        PROP_NAMES.put(250, "geotext.boldfont");
        PROP_NAMES.put(251, "geotext.italicfont");
        PROP_NAMES.put(252, "geotext.underlinefont");
        PROP_NAMES.put(253, "geotext.shadowfont");
        PROP_NAMES.put(254, "geotext.smallcapsfont");
        PROP_NAMES.put(255, "geotext.strikethroughfont");
        PROP_NAMES.put(256, "blip.cropfromtop");
        PROP_NAMES.put(257, "blip.cropfrombottom");
        PROP_NAMES.put(258, "blip.cropfromleft");
        PROP_NAMES.put(259, "blip.cropfromright");
        PROP_NAMES.put(260, "blip.bliptodisplay");
        PROP_NAMES.put(261, "blip.blipfilename");
        PROP_NAMES.put(262, "blip.blipflags");
        PROP_NAMES.put(263, "blip.transparentcolor");
        PROP_NAMES.put(264, "blip.contrastsetting");
        PROP_NAMES.put(265, "blip.brightnesssetting");
        PROP_NAMES.put(266, "blip.gamma");
        PROP_NAMES.put(267, "blip.pictureid");
        PROP_NAMES.put(268, "blip.doublemod");
        PROP_NAMES.put(269, "blip.picturefillmod");
        PROP_NAMES.put(270, "blip.pictureline");
        PROP_NAMES.put(271, "blip.printblip");
        PROP_NAMES.put(272, "blip.printblipfilename");
        PROP_NAMES.put(273, "blip.printflags");
        PROP_NAMES.put(316, "blip.nohittestpicture");
        PROP_NAMES.put(317, "blip.picturegray");
        PROP_NAMES.put(318, "blip.picturebilevel");
        PROP_NAMES.put(319, "blip.pictureactive");
        PROP_NAMES.put(320, "geometry.left");
        PROP_NAMES.put(321, "geometry.top");
        PROP_NAMES.put(322, "geometry.right");
        PROP_NAMES.put(323, "geometry.bottom");
        PROP_NAMES.put(324, "geometry.shapepath");
        PROP_NAMES.put(325, "geometry.vertices");
        PROP_NAMES.put(326, "geometry.segmentinfo");
        PROP_NAMES.put(327, "geometry.adjustvalue");
        PROP_NAMES.put(328, "geometry.adjust2value");
        PROP_NAMES.put(329, "geometry.adjust3value");
        PROP_NAMES.put(330, "geometry.adjust4value");
        PROP_NAMES.put(331, "geometry.adjust5value");
        PROP_NAMES.put(332, "geometry.adjust6value");
        PROP_NAMES.put(333, "geometry.adjust7value");
        PROP_NAMES.put(334, "geometry.adjust8value");
        PROP_NAMES.put(335, "geometry.adjust9value");
        PROP_NAMES.put(336, "geometry.adjust10value");
        PROP_NAMES.put(378, "geometry.shadowOK");
        PROP_NAMES.put(379, "geometry.3dok");
        PROP_NAMES.put(380, "geometry.lineok");
        PROP_NAMES.put(381, "geometry.geotextok");
        PROP_NAMES.put(382, "geometry.fillshadeshapeok");
        PROP_NAMES.put(383, "geometry.fillok");
        PROP_NAMES.put(384, "fill.filltype");
        PROP_NAMES.put(385, "fill.fillcolor");
        PROP_NAMES.put(386, "fill.fillopacity");
        PROP_NAMES.put(387, "fill.fillbackcolor");
        PROP_NAMES.put(388, "fill.backopacity");
        PROP_NAMES.put(389, "fill.crmod");
        PROP_NAMES.put(390, "fill.patterntexture");
        PROP_NAMES.put(391, "fill.blipfilename");
        PROP_NAMES.put(392, "fill.blipflags");
        PROP_NAMES.put(393, "fill.width");
        PROP_NAMES.put(394, "fill.height");
        PROP_NAMES.put(395, "fill.angle");
        PROP_NAMES.put(396, "fill.focus");
        PROP_NAMES.put(397, "fill.toleft");
        PROP_NAMES.put(398, "fill.totop");
        PROP_NAMES.put(399, "fill.toright");
        PROP_NAMES.put(400, "fill.tobottom");
        PROP_NAMES.put(401, "fill.rectleft");
        PROP_NAMES.put(402, "fill.recttop");
        PROP_NAMES.put(403, "fill.rectright");
        PROP_NAMES.put(404, "fill.rectbottom");
        PROP_NAMES.put(405, "fill.dztype");
        PROP_NAMES.put(406, "fill.shadepreset");
        PROP_NAMES.put(407, "fill.shadecolors");
        PROP_NAMES.put(408, "fill.originx");
        PROP_NAMES.put(409, "fill.originy");
        PROP_NAMES.put(410, "fill.shapeoriginx");
        PROP_NAMES.put(411, "fill.shapeoriginy");
        PROP_NAMES.put(412, "fill.shadetype");
        PROP_NAMES.put(443, "fill.filled");
        PROP_NAMES.put(444, "fill.hittestfill");
        PROP_NAMES.put(445, "fill.shape");
        PROP_NAMES.put(446, "fill.userect");
        PROP_NAMES.put(447, "fill.nofillhittest");
        PROP_NAMES.put(448, "linestyle.color");
        PROP_NAMES.put(449, "linestyle.opacity");
        PROP_NAMES.put(450, "linestyle.backcolor");
        PROP_NAMES.put(451, "linestyle.crmod");
        PROP_NAMES.put(452, "linestyle.linetype");
        PROP_NAMES.put(453, "linestyle.fillblip");
        PROP_NAMES.put(454, "linestyle.fillblipname");
        PROP_NAMES.put(455, "linestyle.fillblipflags");
        PROP_NAMES.put(456, "linestyle.fillwidth");
        PROP_NAMES.put(457, "linestyle.fillheight");
        PROP_NAMES.put(458, "linestyle.filldztype");
        PROP_NAMES.put(459, "linestyle.linewidth");
        PROP_NAMES.put(460, "linestyle.linemiterlimit");
        PROP_NAMES.put(461, "linestyle.linestyle");
        PROP_NAMES.put(462, "linestyle.linedashing");
        PROP_NAMES.put(463, "linestyle.linedashstyle");
        PROP_NAMES.put(464, "linestyle.linestartarrowhead");
        PROP_NAMES.put(465, "linestyle.lineendarrowhead");
        PROP_NAMES.put(466, "linestyle.linestartarrowwidth");
        PROP_NAMES.put(467, "linestyle.lineestartarrowlength");
        PROP_NAMES.put(468, "linestyle.lineendarrowwidth");
        PROP_NAMES.put(469, "linestyle.lineendarrowlength");
        PROP_NAMES.put(470, "linestyle.linejoinstyle");
        PROP_NAMES.put(471, "linestyle.lineendcapstyle");
        PROP_NAMES.put(507, "linestyle.arrowheadsok");
        PROP_NAMES.put(508, "linestyle.anyline");
        PROP_NAMES.put(509, "linestyle.hitlinetest");
        PROP_NAMES.put(510, "linestyle.linefillshape");
        PROP_NAMES.put(511, "linestyle.nolinedrawdash");
        PROP_NAMES.put(512, "shadowstyle.type");
        PROP_NAMES.put(513, "shadowstyle.color");
        PROP_NAMES.put(514, "shadowstyle.highlight");
        PROP_NAMES.put(515, "shadowstyle.crmod");
        PROP_NAMES.put(516, "shadowstyle.opacity");
        PROP_NAMES.put(517, "shadowstyle.offsetx");
        PROP_NAMES.put(518, "shadowstyle.offsety");
        PROP_NAMES.put(519, "shadowstyle.secondoffsetx");
        PROP_NAMES.put(520, "shadowstyle.secondoffsety");
        PROP_NAMES.put(521, "shadowstyle.scalextox");
        PROP_NAMES.put(522, "shadowstyle.scaleytox");
        PROP_NAMES.put(523, "shadowstyle.scalextoy");
        PROP_NAMES.put(524, "shadowstyle.scaleytoy");
        PROP_NAMES.put(525, "shadowstyle.perspectivex");
        PROP_NAMES.put(526, "shadowstyle.perspectivey");
        PROP_NAMES.put(527, "shadowstyle.weight");
        PROP_NAMES.put(528, "shadowstyle.originx");
        PROP_NAMES.put(529, "shadowstyle.originy");
        PROP_NAMES.put(574, "shadowstyle.shadow");
        PROP_NAMES.put(575, "shadowstyle.shadowobsured");
        PROP_NAMES.put(576, "perspective.type");
        PROP_NAMES.put(577, "perspective.offsetx");
        PROP_NAMES.put(578, "perspective.offsety");
        PROP_NAMES.put(579, "perspective.scalextox");
        PROP_NAMES.put(580, "perspective.scaleytox");
        PROP_NAMES.put(581, "perspective.scalextoy");
        PROP_NAMES.put(582, "perspective.scaleytox");
        PROP_NAMES.put(583, "perspective.perspectivex");
        PROP_NAMES.put(584, "perspective.perspectivey");
        PROP_NAMES.put(585, "perspective.weight");
        PROP_NAMES.put(586, "perspective.originx");
        PROP_NAMES.put(587, "perspective.originy");
        PROP_NAMES.put(639, "perspective.perspectiveon");
        PROP_NAMES.put(640, "3d.specularamount");
        PROP_NAMES.put(661, "3d.diffuseamount");
        PROP_NAMES.put(662, "3d.shininess");
        PROP_NAMES.put(663, "3d.edgethickness");
        PROP_NAMES.put(664, "3d.extrudeforward");
        PROP_NAMES.put(665, "3d.extrudebackward");
        PROP_NAMES.put(666, "3d.extrudeplane");
        PROP_NAMES.put(667, "3d.extrusioncolor");
        PROP_NAMES.put(648, "3d.crmod");
        PROP_NAMES.put(700, "3d.3deffect");
        PROP_NAMES.put(701, "3d.metallic");
        PROP_NAMES.put(702, "3d.useextrusioncolor");
        PROP_NAMES.put(703, "3d.lightface");
        PROP_NAMES.put(704, "3dstyle.yrotationangle");
        PROP_NAMES.put(705, "3dstyle.xrotationangle");
        PROP_NAMES.put(706, "3dstyle.rotationaxisx");
        PROP_NAMES.put(707, "3dstyle.rotationaxisy");
        PROP_NAMES.put(708, "3dstyle.rotationaxisz");
        PROP_NAMES.put(709, "3dstyle.rotationangle");
        PROP_NAMES.put(710, "3dstyle.rotationcenterx");
        PROP_NAMES.put(711, "3dstyle.rotationcentery");
        PROP_NAMES.put(712, "3dstyle.rotationcenterz");
        PROP_NAMES.put(713, "3dstyle.rendermode");
        PROP_NAMES.put(714, "3dstyle.tolerance");
        PROP_NAMES.put(715, "3dstyle.xviewpoint");
        PROP_NAMES.put(716, "3dstyle.yviewpoint");
        PROP_NAMES.put(717, "3dstyle.zviewpoint");
        PROP_NAMES.put(718, "3dstyle.originx");
        PROP_NAMES.put(719, "3dstyle.originy");
        PROP_NAMES.put(720, "3dstyle.skewangle");
        PROP_NAMES.put(721, "3dstyle.skewamount");
        PROP_NAMES.put(722, "3dstyle.ambientintensity");
        PROP_NAMES.put(723, "3dstyle.keyx");
        PROP_NAMES.put(724, "3dstyle.keyy");
        PROP_NAMES.put(725, "3dstyle.keyz");
        PROP_NAMES.put(726, "3dstyle.keyintensity");
        PROP_NAMES.put(727, "3dstyle.fillx");
        PROP_NAMES.put(728, "3dstyle.filly");
        PROP_NAMES.put(729, "3dstyle.fillz");
        PROP_NAMES.put(730, "3dstyle.fillintensity");
        PROP_NAMES.put(763, "3dstyle.constrainrotation");
        PROP_NAMES.put(764, "3dstyle.rotationcenterauto");
        PROP_NAMES.put(765, "3dstyle.parallel");
        PROP_NAMES.put(766, "3dstyle.keyharsh");
        PROP_NAMES.put(767, "3dstyle.fillharsh");
        PROP_NAMES.put(769, "shape.master");
        PROP_NAMES.put(771, "shape.connectorstyle");
        PROP_NAMES.put(772, "shape.blackandwhitesettings");
        PROP_NAMES.put(773, "shape.wmodepurebw");
        PROP_NAMES.put(774, "shape.wmodebw");
        PROP_NAMES.put(826, "shape.oleicon");
        PROP_NAMES.put(827, "shape.preferrelativeresize");
        PROP_NAMES.put(828, "shape.lockshapetype");
        PROP_NAMES.put(830, "shape.deleteattachedobject");
        PROP_NAMES.put(831, "shape.backgroundshape");
        PROP_NAMES.put(832, "callout.callouttype");
        PROP_NAMES.put(833, "callout.xycalloutgap");
        PROP_NAMES.put(834, "callout.calloutangle");
        PROP_NAMES.put(835, "callout.calloutdroptype");
        PROP_NAMES.put(836, "callout.calloutdropspecified");
        PROP_NAMES.put(837, "callout.calloutlengthspecified");
        PROP_NAMES.put(889, "callout.iscallout");
        PROP_NAMES.put(890, "callout.calloutaccentbar");
        PROP_NAMES.put(891, "callout.callouttextborder");
        PROP_NAMES.put(892, "callout.calloutminusx");
        PROP_NAMES.put(893, "callout.calloutminusy");
        PROP_NAMES.put(894, "callout.dropauto");
        PROP_NAMES.put(895, "callout.lengthspecified");
        PROP_NAMES.put(896, "groupshape.shapename");
        PROP_NAMES.put(897, "groupshape.description");
        PROP_NAMES.put(898, "groupshape.hyperlink");
        PROP_NAMES.put(899, "groupshape.wrappolygonvertices");
        PROP_NAMES.put(900, "groupshape.wrapdistleft");
        PROP_NAMES.put(901, "groupshape.wrapdisttop");
        PROP_NAMES.put(902, "groupshape.wrapdistright");
        PROP_NAMES.put(903, "groupshape.wrapdistbottom");
        PROP_NAMES.put(904, "groupshape.regroupid");
        PROP_NAMES.put(953, "groupshape.editedwrap");
        PROP_NAMES.put(954, "groupshape.behinddocument");
        PROP_NAMES.put(955, "groupshape.ondblclicknotify");
        PROP_NAMES.put(956, "groupshape.isbutton");
        PROP_NAMES.put(957, "groupshape.1dadjustment");
        PROP_NAMES.put(958, "groupshape.hidden");
        PROP_NAMES.put(959, "groupshape.print");
    };

    public EscherDump() {
        //
    }

    /**
     * Decodes the escher stream from a byte array and dumps the results to
     * a print stream.
     *
     * @param data      The data array containing the escher records.
     * @param offset    The starting offset within the data array.
     * @param size      The number of bytes to read.
     * @param out       The output stream to write the results to.
     *
     */
    public void dump(byte[] data, int offset, int size, PrintStream out) {
        EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int pos = offset;
        while ( pos < offset + size )
        {
            EscherRecord r = recordFactory.createRecord(data, pos);
            int bytesRead = r.fillFields(data, pos, recordFactory );
            out.println( r.toString() );
            pos += bytesRead;
        }
    }

    /**
     * This version of dump is a translation from the open office escher dump routine.
     *
     * @param maxLength The number of bytes to read
     * @param in        An input stream to read from.
     * @param out       An output stream to write to.
     */
    public void dumpOld(long maxLength, InputStream in, PrintStream out)
            throws IOException, LittleEndian.BufferUnderrunException {
        long remainingBytes = maxLength;
        short options;      // 4 bits for the version and 12 bits for the instance
        short recordId;
        int recordBytesRemaining;       // including enclosing records
        StringBuffer stringBuf = new StringBuffer();
        short nDumpSize;
        String recordName;

        boolean atEOF = false;

        while (!atEOF && (remainingBytes > 0)) {
            stringBuf = new StringBuffer();
            options = LittleEndian.readShort( in );
            recordId = LittleEndian.readShort( in );
            recordBytesRemaining = LittleEndian.readInt( in );

            remainingBytes -= 2 + 2 + 4;

            switch ( recordId )
            {
                case (short) 0xF000:
                    recordName = "MsofbtDggContainer";
                    break;
                case (short) 0xF006:
                    recordName = "MsofbtDgg";
                    break;
                case (short) 0xF016:
                    recordName = "MsofbtCLSID";
                    break;
                case (short) 0xF00B:
                    recordName = "MsofbtOPT";
                    break;
                case (short) 0xF11A:
                    recordName = "MsofbtColorMRU";
                    break;
                case (short) 0xF11E:
                    recordName = "MsofbtSplitMenuColors";
                    break;
                case (short) 0xF001:
                    recordName = "MsofbtBstoreContainer";
                    break;
                case (short) 0xF007:
                    recordName = "MsofbtBSE";
                    break;
                case (short) 0xF002:
                    recordName = "MsofbtDgContainer";
                    break;
                case (short) 0xF008:
                    recordName = "MsofbtDg";
                    break;
                case (short) 0xF118:
                    recordName = "MsofbtRegroupItem";
                    break;
                case (short) 0xF120:
                    recordName = "MsofbtColorScheme";
                    break;
                case (short) 0xF003:
                    recordName = "MsofbtSpgrContainer";
                    break;
                case (short) 0xF004:
                    recordName = "MsofbtSpContainer";
                    break;
                case (short) 0xF009:
                    recordName = "MsofbtSpgr";
                    break;
                case (short) 0xF00A:
                    recordName = "MsofbtSp";
                    break;
                case (short) 0xF00C:
                    recordName = "MsofbtTextbox";
                    break;
                case (short) 0xF00D:
                    recordName = "MsofbtClientTextbox";
                    break;
                case (short) 0xF00E:
                    recordName = "MsofbtAnchor";
                    break;
                case (short) 0xF00F:
                    recordName = "MsofbtChildAnchor";
                    break;
                case (short) 0xF010:
                    recordName = "MsofbtClientAnchor";
                    break;
                case (short) 0xF011:
                    recordName = "MsofbtClientData";
                    break;
                case (short) 0xF11F:
                    recordName = "MsofbtOleObject";
                    break;
                case (short) 0xF11D:
                    recordName = "MsofbtDeletedPspl";
                    break;
                case (short) 0xF005:
                    recordName = "MsofbtSolverContainer";
                    break;
                case (short) 0xF012:
                    recordName = "MsofbtConnectorRule";
                    break;
                case (short) 0xF013:
                    recordName = "MsofbtAlignRule";
                    break;
                case (short) 0xF014:
                    recordName = "MsofbtArcRule";
                    break;
                case (short) 0xF015:
                    recordName = "MsofbtClientRule";
                    break;
                case (short) 0xF017:
                    recordName = "MsofbtCalloutRule";
                    break;
                case (short) 0xF119:
                    recordName = "MsofbtSelection";
                    break;
                case (short) 0xF122:
                    recordName = "MsofbtUDefProp";
                    break;
                default:
                    if ( recordId >= (short) 0xF018 && recordId <= (short) 0xF117 )
                        recordName = "MsofbtBLIP";
                    else if ( ( options & (short) 0x000F ) == (short) 0x000F )
                        recordName = "UNKNOWN container";
                    else
                        recordName = "UNKNOWN ID";
            }

            stringBuf.append( "  " );
            stringBuf.append( HexDump.toHex( recordId ) );
            stringBuf.append( "  " ).append( recordName ).append( " [" );
            stringBuf.append( HexDump.toHex( options ) );
            stringBuf.append( ',' );
            stringBuf.append( HexDump.toHex( recordBytesRemaining ) );
            stringBuf.append( "]  instance: " );
            stringBuf.append( HexDump.toHex( ( (short) ( options >> 4 ) ) ) );
            out.println( stringBuf.toString() );


            if ( recordId == (short) 0xF007 && 36 <= remainingBytes && 36 <= recordBytesRemaining )
            {	// BSE, FBSE
                //                ULONG nP = pIn->GetRecPos();

                byte n8;
                //                short n16;
                //                int n32;

                stringBuf = new StringBuffer( "    btWin32: " );
                n8 = (byte) in.read();
                stringBuf.append( HexDump.toHex( n8 ) );
                stringBuf.append( getBlipType( n8 ) );
                stringBuf.append( "  btMacOS: " );
                n8 = (byte) in.read();
                stringBuf.append( HexDump.toHex( n8 ) );
                stringBuf.append( getBlipType( n8 ) );
                out.println( stringBuf.toString() );

                out.println( "    rgbUid:" );
                HexDump.dump( in, out, 0, 16 );

                out.print( "    tag: " );
                outHex( 2, in, out );
                out.println();
                out.print( "    size: " );
                outHex( 4, in, out );
                out.println();
                out.print( "    cRef: " );
                outHex( 4, in, out );
                out.println();
                out.print( "    offs: " );
                outHex( 4, in, out );
                out.println();
                out.print( "    usage: " );
                outHex( 1, in, out );
                out.println();
                out.print( "    cbName: " );
                outHex( 1, in, out );
                out.println();
                out.print( "    unused2: " );
                outHex( 1, in, out );
                out.println();
                out.print( "    unused3: " );
                outHex( 1, in, out );
                out.println();

                // subtract the number of bytes we've read
                remainingBytes -= 36;
                //n -= pIn->GetRecPos() - nP;
                recordBytesRemaining = 0;		// loop to MsofbtBLIP
            }
            else if ( recordId == (short) 0xF010 && 0x12 <= remainingBytes && 0x12 <= recordBytesRemaining )
            {	// ClientAnchor
                //ULONG nP = pIn->GetRecPos();
                //                short n16;

                out.print( "    Flag: " );
                outHex( 2, in, out );
                out.println();
                out.print( "    Col1: " );
                outHex( 2, in, out );
                out.print( "    dX1: " );
                outHex( 2, in, out );
                out.print( "    Row1: " );
                outHex( 2, in, out );
                out.print( "    dY1: " );
                outHex( 2, in, out );
                out.println();
                out.print( "    Col2: " );
                outHex( 2, in, out );
                out.print( "    dX2: " );
                outHex( 2, in, out );
                out.print( "    Row2: " );
                outHex( 2, in, out );
                out.print( "    dY2: " );
                outHex( 2, in, out );
                out.println();

                remainingBytes -= 18;
                recordBytesRemaining -= 18;

            }
            else if ( recordId == (short) 0xF00B || recordId == (short) 0xF122 )
            {	// OPT
                int nComplex = 0;
                out.println( "    PROPID        VALUE" );
                while ( recordBytesRemaining >= 6 + nComplex && remainingBytes >= 6 + nComplex )
                {
                    short n16;
                    int n32;
                    n16 = LittleEndian.readShort( in );
                    n32 = LittleEndian.readInt( in );

                    recordBytesRemaining -= 6;
                    remainingBytes -= 6;
                    out.print( "    " );
                    out.print( HexDump.toHex( n16 ) );
                    out.print( " (" );
                    int propertyId = n16 & (short) 0x3FFF;
                    out.print( " " + propertyId  );
                    if ( ( n16 & (short) 0x8000 ) == 0 )
                    {
                        if ( ( n16 & (short) 0x4000 ) != 0 )
                            out.print( ", fBlipID" );
                        out.print( ")  " );

                        out.print( HexDump.toHex( n32 ) );

                        if ( ( n16 & (short) 0x4000 ) == 0 )
                        {
                            out.print( " (" );
                            out.print( dec1616( n32 ) );
                            out.print( ')' );
                            out.print( " {" + propName( (short)propertyId ) + "}" );
                        }
                        out.println();
                    }
                    else
                    {
                        out.print( ", fComplex)  " );
                        out.print( HexDump.toHex( n32 ) );
                        out.print( " - Complex prop len" );
                        out.println( " {" + propName( (short)propertyId ) + "}" );

                        nComplex += n32;
                    }

                }
                // complex property data
                while ( ( nComplex & remainingBytes ) > 0 )
                {
                    nDumpSize = ( nComplex > (int) remainingBytes ) ? (short) remainingBytes : (short) nComplex;
                    HexDump.dump( in, out, 0, nDumpSize );
                    nComplex -= nDumpSize;
                    recordBytesRemaining -= nDumpSize;
                    remainingBytes -= nDumpSize;
                }
            }
            else if ( recordId == (short) 0xF012 )
            {
                out.print( "    Connector rule: " );
                out.print( LittleEndian.readInt( in ) );
                out.print( "    ShapeID A: " );
                out.print( LittleEndian.readInt( in ) );
                out.print( "   ShapeID B: " );
                out.print( LittleEndian.readInt( in ) );
                out.print( "    ShapeID connector: " );
                out.print( LittleEndian.readInt( in ) );
                out.print( "   Connect pt A: " );
                out.print( LittleEndian.readInt( in ) );
                out.print( "   Connect pt B: " );
                out.println( LittleEndian.readInt( in ) );

                recordBytesRemaining -= 24;
                remainingBytes -= 24;
            }
            else if ( recordId >= (short) 0xF018 && recordId < (short) 0xF117 )
            {
                out.println( "    Secondary UID: " );
                HexDump.dump( in, out, 0, 16 );
                out.println( "    Cache of size: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Boundary top: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Boundary left: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Boundary width: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Boundary height: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    X: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Y: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Cache of saved size: " + HexDump.toHex( LittleEndian.readInt( in ) ) );
                out.println( "    Compression Flag: " + HexDump.toHex( (byte) in.read() ) );
                out.println( "    Filter: " + HexDump.toHex( (byte) in.read() ) );
                out.println( "    Data (after decompression): " );

                recordBytesRemaining -= 34 + 16;
                remainingBytes -= 34 + 16;

                nDumpSize = ( recordBytesRemaining > (int) remainingBytes ) ? (short) remainingBytes : (short) recordBytesRemaining;


                byte[] buf = new byte[nDumpSize];
                int read = in.read( buf );
                while ( read != -1 && read < nDumpSize )
                    read += in.read( buf, read, buf.length );
                ByteArrayInputStream bin = new ByteArrayInputStream( buf );

                InputStream in1 = new InflaterInputStream( bin );
                int bytesToDump = -1;
                HexDump.dump( in1, out, 0, bytesToDump );

                recordBytesRemaining -= nDumpSize;
                remainingBytes -= nDumpSize;

            }

            boolean isContainer = ( options & (short) 0x000F ) == (short) 0x000F;
            if ( isContainer && remainingBytes >= 0 )
            {	// Container
                if ( recordBytesRemaining <= (int) remainingBytes )
                    out.println( "            completed within" );
                else
                    out.println( "            continued elsewhere" );
            }
            else if ( remainingBytes >= 0 )
            // -> 0x0000 ... 0x0FFF
            {
                nDumpSize = ( recordBytesRemaining > (int) remainingBytes ) ? (short) remainingBytes : (short) recordBytesRemaining;

                if ( nDumpSize != 0 )
                {
                    HexDump.dump( in, out, 0, nDumpSize );
                    remainingBytes -= nDumpSize;
                }
            }
            else
                out.println( " >> OVERRUN <<" );
        }

    }

    /**
     * Returns a property name given a property id.  This is used only by the
     * old escher dump routine.
     *
     * @param propertyId    The property number for the name
     * @return  A descriptive name.
     */
    private String propName(short propertyId) {
        final String n = PROP_NAMES.get(Integer.valueOf(propertyId));
        if (n == null) {
            return "unknown property";
        }
        return n;
    }

    /**
     * Returns the blip description given a blip id.
     *
     * @param   b   blip id
     * @return  A description.
     */
    private static String getBlipType(byte b) {
        return EscherBSERecord.getBlipType(b);
    }

    /**
     * Straight conversion from OO.  Converts a type of float.
     */
    private String dec1616( int n32 )
    {
        String result = "";
        result += (short) ( n32 >> 16 );
        result += '.';
        result += (short) ( n32 & (short) 0xFFFF );
        return result;
    }

    /**
     * Dumps out a hex value by reading from a input stream.
     *
     * @param bytes     How many bytes this hex value consists of.
     * @param in        The stream to read the hex value from.
     * @param out       The stream to write the nicely formatted hex value to.
     */
    private void outHex( int bytes, InputStream in, PrintStream out ) throws IOException, LittleEndian.BufferUnderrunException
    {
        switch ( bytes )
        {
            case 1:
                out.print( HexDump.toHex( (byte) in.read() ) );
                break;
            case 2:
                out.print( HexDump.toHex( LittleEndian.readShort( in ) ) );
                break;
            case 4:
                out.print( HexDump.toHex( LittleEndian.readInt( in ) ) );
                break;
            default:
                throw new IOException( "Unable to output variable of that width" );
        }
    }

    /**
     * A simple test stub.
     */
    public static void main( String[] args ) {
        String dump =
                "0F 00 00 F0 89 07 00 00 00 00 06 F0 18 00 00 00 " +
                "05 04 00 00 02 00 00 00 05 00 00 00 01 00 00 00 " +
                "01 00 00 00 05 00 00 00 4F 00 01 F0 2F 07 00 00 " +
                "42 00 07 F0 B7 01 00 00 03 04 3F 14 AE 6B 0F 65 " +
                "B0 48 BF 5E 94 63 80 E8 91 73 FF 00 93 01 00 00 " +
                "01 00 00 00 00 00 00 00 00 00 FF FF 20 54 1C F0 " +
                "8B 01 00 00 3F 14 AE 6B 0F 65 B0 48 BF 5E 94 63 " +
                "80 E8 91 73 92 0E 00 00 00 00 00 00 00 00 00 00 " +
                "D1 07 00 00 DD 05 00 00 4A AD 6F 00 8A C5 53 00 " +
                "59 01 00 00 00 FE 78 9C E3 9B C4 00 04 AC 77 D9 " +
                "2F 32 08 32 FD E7 61 F8 FF 0F C8 FD 05 C5 30 19 " +
                "10 90 63 90 FA 0F 06 0C 8C 0C 5C 70 19 43 30 EB " +
                "0E FB 05 86 85 0C DB 18 58 80 72 8C 70 16 0B 83 " +
                "05 56 51 29 88 C9 60 D9 69 0C 6C 20 26 23 03 C8 " +
                "74 B0 A8 0E 03 07 FB 45 56 C7 A2 CC C4 1C 06 66 " +
                "A0 0D 2C 40 39 5E 86 4C 06 3D A0 4E 10 D0 60 D9 " +
                "C8 58 CC E8 CF B0 80 61 3A 8A 7E 0D C6 23 AC 4F " +
                "E0 E2 98 B6 12 2B 06 73 9D 12 E3 52 56 59 F6 08 " +
                "8A CC 52 66 A3 50 FF 96 2B 94 E9 DF 4C A1 FE 2D " +
                "3A 03 AB 9F 81 C2 F0 A3 54 BF 0F 85 EE A7 54 FF " +
                "40 FB 7F A0 E3 9F D2 F4 4F 71 FE 19 58 FF 2B 31 " +
                "7F 67 36 3B 25 4F 99 1B 4E 53 A6 5F 89 25 95 E9 " +
                "C4 00 C7 83 12 F3 1F 26 35 4A D3 D2 47 0E 0A C3 " +
                "41 8E C9 8A 52 37 DC 15 A1 D0 0D BC 4C 06 0C 2B " +
                "28 2C 13 28 D4 EF 43 61 5A A0 58 3F 85 71 E0 4B " +
                "69 9E 64 65 FE 39 C0 E5 22 30 1D 30 27 0E 74 3A " +
                "18 60 FD 4A CC B1 2C 13 7D 07 36 2D 2A 31 85 B2 " +
                "6A 0D 74 1D 1D 22 4D 99 FE 60 0A F5 9B EC 1C 58 " +
                "FD 67 06 56 3F 38 0D 84 3C A5 30 0E 28 D3 AF C4 " +
                "A4 CA FA 44 7A 0D 65 6E 60 7F 4D A1 1B 24 58 F7 " +
                "49 AF A5 CC 0D CC DF 19 FE 03 00 F0 B1 25 4D 42 " +
                "00 07 F0 E1 01 00 00 03 04 39 50 BE 98 B0 6F 57 " +
                "24 31 70 5D 23 2F 9F 10 66 FF 00 BD 01 00 00 01 " +
                "00 00 00 00 00 00 00 00 00 FF FF 20 54 1C F0 B5 " +
                "01 00 00 39 50 BE 98 B0 6F 57 24 31 70 5D 23 2F " +
                "9F 10 66 DA 03 00 00 00 00 00 00 00 00 00 00 D1 " +
                "07 00 00 DD 05 00 00 4A AD 6F 00 8A C5 53 00 83 " +
                "01 00 00 00 FE 78 9C A5 52 BF 4B 42 51 14 3E F7 " +
                "DC 77 7A 16 45 48 8B 3C 48 A8 16 15 0D 6C 88 D0 " +
                "04 C3 40 A3 32 1C 84 96 08 21 04 A1 C5 5C A2 35 " +
                "82 C0 35 6A AB 1C 6A 6B A8 24 5A 83 68 08 84 84 " +
                "96 A2 86 A0 7F C2 86 5E E7 5E F5 41 E4 10 BC 03 " +
                "1F E7 FB F1 CE B9 F7 F1 9E 7C 05 2E 7A 37 9B E0 " +
                "45 7B 10 EC 6F 96 5F 1D 74 13 55 7E B0 6C 5D 20 " +
                "60 C0 49 A2 9A BD 99 4F 50 83 1B 30 38 13 0E 33 " +
                "60 A6 A7 6B B5 37 EB F4 10 FA 14 15 A0 B6 6B 37 " +
                "0C 1E B3 49 73 5B A5 C2 26 48 3E C1 E0 6C 08 4A " +
                "30 C9 93 AA 02 B8 20 13 62 05 4E E1 E8 D7 7C C0 " +
                "B8 14 95 5E BE B8 A7 CF 1E BE 55 2C 56 B9 78 DF " +
                "08 7E 88 4C 27 FF 7B DB FF 7A DD B7 1A 17 67 34 " +
                "6A AE BA DA 35 D1 E7 72 BE FE EC 6E FE DA E5 7C " +
                "3D EC 7A DE 03 FD 50 06 0B 23 F2 0E F3 B2 A5 11 " +
                "91 0D 4C B5 B5 F3 BF 94 C1 8F 24 F7 D9 6F 60 94 " +
                "3B C9 9A F3 1C 6B E7 BB F0 2E 49 B2 25 2B C6 B1 " +
                "EE 69 EE 15 63 4F 71 7D CE 85 CC C8 35 B9 C3 28 " +
                "28 CE D0 5C 67 79 F2 4A A2 14 23 A4 38 43 73 9D " +
                "2D 69 2F C1 08 31 9F C5 5C 9B EB 7B C5 69 19 B3 " +
                "B4 81 F3 DC E3 B4 8E 8B CC B3 94 53 5A E7 41 2A " +
                "63 9A AA 38 C5 3D 48 BB EC 57 59 6F 2B AD 73 1F " +
                "1D 60 92 AE 70 8C BB 8F CE 31 C1 3C 49 27 4A EB " +
                "DC A4 5B 8C D1 0B 0E 73 37 E9 11 A7 99 C7 E8 41 " +
                "69 B0 7F 00 96 F2 A7 E8 42 00 07 F0 B4 01 00 00 " +
                "03 04 1A BA F9 D6 A9 B9 3A 03 08 61 E9 90 FF 7B " +
                "9E E6 FF 00 90 01 00 00 01 00 00 00 00 00 00 00 " +
                "00 00 FF FF 20 54 1C F0 88 01 00 00 1A BA F9 D6 " +
                "A9 B9 3A 03 08 61 E9 90 FF 7B 9E E6 12 0E 00 00 " +
                "00 00 00 00 00 00 00 00 D1 07 00 00 DD 05 00 00 " +
                "4A AD 6F 00 8A C5 53 00 56 01 00 00 00 FE 78 9C " +
                "E3 13 62 00 02 D6 BB EC 17 19 04 99 FE F3 30 FC " +
                "FF 07 E4 FE 82 62 98 0C 08 C8 31 48 FD 07 03 06 " +
                "46 06 2E B8 8C 21 98 75 87 FD 02 C3 42 86 6D 0C " +
                "2C 40 39 46 38 8B 85 C1 02 AB A8 14 C4 64 B0 EC " +
                "34 06 36 10 93 91 01 64 3A 58 54 87 81 83 FD 22 " +
                "AB 63 51 66 62 0E 03 33 D0 06 16 A0 1C 2F 43 26 " +
                "83 1E 50 27 08 68 B0 6C 64 2C 66 F4 67 58 C0 30 " +
                "1D 45 BF 06 E3 11 D6 27 70 71 4C 5B 89 15 83 B9 " +
                "4E 89 71 29 AB 2C 7B 04 45 66 29 B3 51 A8 7F CB " +
                "15 CA F4 6F A6 50 FF 16 9D 81 D5 CF 40 61 F8 51 " +
                "AA DF 87 42 F7 53 AA 7F A0 FD 3F D0 F1 4F 69 FA " +
                "A7 38 FF 0C AC FF 95 98 BF 33 9B 9D 92 A7 CC 0D " +
                "A7 29 D3 AF C4 92 CA 74 62 80 E3 41 89 F9 0F 93 " +
                "1A A5 69 E9 23 07 85 E1 20 C7 64 45 A9 1B EE 8A " +
                "50 E8 06 5E 26 03 86 15 14 96 09 14 EA F7 A1 30 " +
                "2D 50 AC 9F C2 38 F0 A5 34 4F B2 32 FF 1C E0 72 " +
                "11 98 0E 98 13 07 38 1D 28 31 C7 B2 4C F4 1D D8 " +
                "B4 A0 C4 14 CA AA 35 D0 75 64 88 34 65 FA 83 29 " +
                "D4 6F B2 73 60 F5 9F A1 54 FF 0E CA D3 40 C8 53 " +
                "0A E3 E0 09 85 6E 50 65 7D 22 BD 86 32 37 B0 BF " +
                "A6 D0 0D 12 AC FB A4 D7 52 E6 06 E6 EF 0C FF 01 " +
                "97 1D 12 C7 42 00 07 F0 C3 01 00 00 03 04 BA 4C " +
                "B6 23 BA 8B 27 BE C8 55 59 86 24 9F 89 D4 FF 00 " +
                "9F 01 00 00 01 00 00 00 00 00 00 00 00 00 FF FF " +
                "20 54 1C F0 97 01 00 00 BA 4C B6 23 BA 8B 27 BE " +
                "C8 55 59 86 24 9F 89 D4 AE 0E 00 00 00 00 00 00 " +
                "00 00 00 00 D1 07 00 00 DD 05 00 00 4A AD 6F 00 " +
                "8A C5 53 00 65 01 00 00 00 FE 78 9C E3 5B C7 00 " +
                "04 AC 77 D9 2F 32 08 32 FD E7 61 F8 FF 0F C8 FD " +
                "05 C5 30 19 10 90 63 90 FA 0F 06 0C 8C 0C 5C 70 " +
                "19 43 30 EB 0E FB 05 86 85 0C DB 18 58 80 72 8C " +
                "70 16 0B 83 05 56 51 29 88 C9 60 D9 69 0C 6C 20 " +
                "26 23 03 C8 74 B0 A8 0E 03 07 FB 45 56 C7 A2 CC " +
                "C4 1C 06 66 A0 0D 2C 40 39 5E 86 4C 06 3D A0 4E " +
                "10 D0 60 99 C6 B8 98 D1 9F 61 01 C3 74 14 FD 1A " +
                "8C 2B D8 84 B1 88 4B A5 A5 75 03 01 50 DF 59 46 " +
                "77 46 0F A8 3C A6 AB 88 15 83 B9 5E 89 B1 8B D5 " +
                "97 2D 82 22 B3 94 29 D5 BF E5 CA C0 EA DF AC 43 " +
                "A1 FD 14 EA 67 A0 30 FC 28 D5 EF 43 A1 FB 7D 87 " +
                "B8 FF 07 3A FE 07 3A FD 53 EA 7E 0A C3 4F 89 F9 " +
                "0E 73 EA 69 79 CA DC 70 8A 32 FD 4A 2C 5E 4C DF " +
                "87 7A 3C BC E0 A5 30 1E 3E 31 C5 33 AC A0 30 2F " +
                "52 A8 DF 87 C2 30 A4 54 3F A5 65 19 85 65 A9 12 " +
                "D3 2B 16 0D 8A CB 13 4A F3 E3 27 E6 09 03 9D 0E " +
                "06 58 BF 12 B3 13 CB C1 01 4E 8B 4A 4C 56 AC 91 " +
                "03 5D 37 86 48 53 A6 3F 98 42 FD 26 3B 07 56 FF " +
                "99 1D 14 EA A7 CC 7E 70 1A 08 79 42 61 1C 3C A5 " +
                "D0 0D 9C 6C C2 32 6B 29 73 03 DB 6B CA DC C0 F8 " +
                "97 F5 AD CC 1A CA DC C0 F4 83 32 37 B0 A4 30 CE " +
                "FC C7 48 99 1B FE 33 32 FC 07 00 6C CC 2E 23 33 " +
                "00 0B F0 12 00 00 00 BF 00 08 00 08 00 81 01 09 " +
                "00 00 08 C0 01 40 00 00 08 40 00 1E F1 10 00 00 " +
                "00 0D 00 00 08 0C 00 00 08 17 00 00 08 F7 00 00 " +
                "10                                              ";

        // Decode the stream to bytes
        byte[] bytes = HexRead.readFromString(dump);
        // Create a new instance of the escher dumper
        EscherDump dumper = new EscherDump();
        // Dump the contents of scher to screen.
//        dumper.dumpOld( bytes.length, new ByteArrayInputStream( bytes ), System.out );
        dumper.dump(bytes, 0, bytes.length, System.out);

    }

    public void dump( int recordSize, byte[] data, PrintStream out ) {
        dump( data, 0, recordSize, out );
    }
}
