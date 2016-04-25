// ralaVersion 1.2.0
// Methods from Subnettable 1.2.6 [Origin: 1.1.8]
package at.rala;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

// mini bug: toString: " \t" between -> tab removed

/**
 * IP-Address and Subnetmask needed to get a Subnet<br>
 * <br>
 * this tool is in use from rala for his program: "Subnettable" (German: "Subnetztabelle")<br>
 * <p>
 * <i>you should catch IllegalArgumentExceptions</i>
 *
 * @author rala<br>
 *         <a href="mailto:code@rala.io">code@rala.io</a><br>
 *         <a href="www.rala.io">www.rala.io</a>
 * @version 1.3.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Subnet implements Comparable<Subnet> {
    private static final int errorNumber = 0xDEADBEEF;
    private static final int[] snm_allowed_int = {0, 128, 192, 224, 240, 248, 252, 254, 255, 256};
    // 255: no usable hosts, 256: only 1 host

    /**
     * <p>
     * easy to check with <i>{@link String#startsWith(String)}</i> and <i>{@link String#endsWith(String)}</i>
     * </p>
     * <p>
     * if the IP throws the error: message ends with "[IP]"<br>
     * if the SNM throws the error: message ends with "[SNM]"
     * </p>
     */
    public static final String Exception_message = "Subnet Error - ";
    public static final String IllegalArgument_EntryMissing = Exception_message + "Entry missing - maybe the entry is \"\" or \" \"";
    public static final String IllegalArgument_EntryNotSupported = Exception_message + "Entry not supported / probably contains wrong characters: check it again!";
    public static final String IllegalArgument_EntrySizeToSmall = Exception_message + "Size of the entry is to small";
    public static final String IllegalArgument_EntrySizeToLarge = Exception_message + "Size of the entry is to large";

    public static final String IllegalArgument_SubnetmaskFirstQuadIsInteresting = "First Quad is not allowed to be the interesting one";
    public static final String IllegalArgument_SubnetmaskContainsWrongNumber = "Subnetmask contains wrong number";
    public static final String IllegalArgument_Subnetmask255to0 = "Subnetmask: unequal 255 -> next has to be 0";

    public static final String IllegalArgument_FirstQuadIsNotTheSame = Exception_message + "Summarize Exception: please make sure that both have the same Network (1. Quad)";

    private String IP = "";
    private String SNM = "";
    private String[] IP_a = new String[4];
    private String[] SNM_a = new String[4];
    private final String[] WILD_a = new String[4];
    private int IQ = -1;
    private int MZ = -1;
    private int MZ_min = -1;
    private int MZ_max = -1;

    private final String[] subnetID_a = new String[4];
    private final String[] firstAvailableIP_a = new String[4];
    private final String[] lastAvailableIP_a = new String[4];
    private final String[] broadCastIP_a = new String[4];
    private final String[] classID_a = new String[4];
    private final String[] classSNM_a = new String[4];

    private char classChar = ' ';

    private int netbits;
    private String netbits_s = "";
    private int subnetbits;
    private String subnetbits_s = "";
    private int hostbits;
    private String hostbits_s = "";
    private int countOfSubnets;
    private String countOfSubnets_s = "";
    private int countOfHosts;
    private String countOfHosts_s = "";

    /**
     * generate a Subnet
     *
     * @param IP  IP-Address
     * @param SNM Subnetmask
     * @since 1.0.0
     */
    public Subnet(String IP, String SNM) {
        setIP(IP, false);
        setSNM(SNM);
    }

    /**
     * generate a Subnet with Arrays
     *
     * @param IP  IP-Address [4]
     * @param SNM Subnetmask [4]
     * @since 1.0.0
     */
    public Subnet(String[] IP, String[] SNM) {
        setIP(IP, false);
        setSNM(SNM);
    }

    /**
     * generate a Subnet with Arrays
     *
     * @param IP  IP-Address [4]
     * @param SNM Subnetmask [4]
     * @since 1.3.0
     */
    public Subnet(int[] IP, int[] SNM) {
        setIP(IP, false);
        setSNM(Arrays.toString(SNM).replaceAll("[\\[\\]]", "").split("\\s*,\\s*"));
    }

    /**
     * set IP to <i>iP</i><br>
     * and recalculate
     *
     * @param iP IP-Address
     * @return if entry is ok
     * @since 1.0.0
     */
    public boolean setIP(String iP) {
        return setIP(iP, true);
    }

    /**
     * set IP to <i>iP</i>
     *
     * @param iP          IP-Address
     * @param reCalculate recalculate now or later..?
     * @return if entry is ok
     * @since 1.0.0
     */
    public boolean setIP(String iP, boolean reCalculate) {
        if (iP.trim().equals("")) {
            throw new IllegalArgumentException(IllegalArgument_EntryMissing + " [IP]");
        }
        iP = addEnd0(iP);

        IP = iP;
        IP_a = iP.split("\\.");
        checkEntry(IP_a, true);
        if (reCalculate) {
            setSNM(SNM);
        }
        return true;
    }

    /**
     * set IP-Address
     *
     * @param iP_a IP-Address Array [4]
     * @return if entry is ok
     * @since 1.0.0
     */
    public boolean setIP(String[] iP_a) {
        return setIP(networkArrayToString(iP_a));
    }

    /**
     * set IP to <i>iP</i>
     *
     * @param iP_a        IP-Address
     * @param reCalculate recalculate now or later..?
     * @return if entry is ok
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.0.0
     */
    public boolean setIP(String[] iP_a, boolean reCalculate) {
        String iP_s = networkArrayToString(iP_a);
        return setIP(iP_s, reCalculate);
    }

    /**
     * set IP-Address
     *
     * @param iP_a IP-Address Array [4]
     * @return if entry is ok
     * @since 1.3.0
     */
    public boolean setIP(int[] iP_a) {
        return setIP(networkArrayToString(integerArrayToStringArray(iP_a)));
    }

    /**
     * set IP to <i>iP</i>
     *
     * @param iP_a        IP-Address
     * @param reCalculate recalculate now or later..?
     * @return if entry is ok
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.3.0
     */
    public boolean setIP(int[] iP_a, boolean reCalculate) {
        String iP_s = networkArrayToString(integerArrayToStringArray(iP_a));
        return setIP(iP_s, reCalculate);
    }

    /**
     * set subnetmask &amp; recalculate table NOW<br>
     *
     * @param sNM subnetmask
     * @return if entry is ok
     * @since 1.0.0
     */
    public boolean setSNM(String sNM) {
        if (sNM.trim().equals("")) {
            throw new IllegalArgumentException(IllegalArgument_EntryMissing + " [SNM]");
        }
        sNM = addEnd0(sNM);

        SNM = sNM;
        SNM_a = sNM.split("\\.");
        checkEntry(SNM_a, false);
        calc();
        return true;
    }

    /**
     * set subnetmask &amp; recalculate table NOW<br>
     *
     * @param sNM_a subnetmask Array [4]
     * @return if entry is ok
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.0.0
     */
    public boolean setSNM(String[] sNM_a) {
        return setSNM(networkArrayToString(sNM_a));
    }

    /**
     * set subnetmask &amp; recalculate table NOW<br>
     *
     * @param sNM_a subnetmask Array [4]
     * @return if entry is ok
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.3.0
     */
    public boolean setSNM(int[] sNM_a) {
        return setSNM(networkArrayToString(integerArrayToStringArray(sNM_a)));
    }

    /**
     * @return IP-Address
     * @since 1.0.0
     */
    public String getIP() {
        return IP;
    }

    /**
     * @return IP-Address as array
     * @since 1.0.0
     */
    public String[] getIP_array() {
        return IP_a;
    }

    /**
     * @return subnetmask
     * @see Subnet#getSubnetmask()
     * @since 1.0.0
     */
    public String getSNM() {
        return SNM;
    }

    /**
     * @return subnetmask
     * @see Subnet#getSubnetmask()
     * @since 1.0.0
     */
    public String getSubnetmask() {
        return SNM;
    }

    /**
     * @return subnetmask as array
     * @see Subnet#getSubnetmask_array()
     * @since 1.0.0
     */
    public String[] getSNM_array() {
        return SNM_a;
    }

    /**
     * @return subnetmask as array
     * @see Subnet#getSubnetmask_array()
     * @since 1.0.0
     */
    public String[] getSubnetmask_array() {
        return SNM_a;
    }

    /**
     * @return WildmarkMask
     * @since 1.0.0
     */
    public String getWildmarkMask() {
        return networkArrayToString(WILD_a);
    }

    /**
     * @return WildmarkMask as array
     * @since 1.0.0
     */
    public String[] getWildmarkMask_array() {
        return WILD_a;
    }

    /**
     * @return IQ from Array (0-3 NOT 1-4!!)
     * @since 1.0.0
     */
    public int getIQ() {
        return IQ;
    }

    /**
     * @return magic number
     * @since 1.0.0
     */
    public int getMagicNumber() {
        return MZ;
    }

    /**
     * <b><i>use it only if you know what you do!</i></b>
     *
     * @return magic number - minimum
     * @since 1.0.0
     */
    public int getMagicNumber_min() {
        return MZ_min;
    }

    /**
     * <b><i>use it only if you know what you do!</i></b>
     *
     * @return magic number - maximum
     * @since 1.0.0
     */
    public int getMagicNumber_max() {
        return MZ_max;
    }

    /**
     * @return Subnet ID
     * @since 1.0.0
     */
    public String getSubnetID() {
        return networkArrayToString(getSubnetID_array());
    }

    /**
     * @return Subnet ID as array
     * @since 1.0.0
     */
    public String[] getSubnetID_array() {
        return subnetID_a;
    }

    /**
     * @return First available IP address
     * @since 1.0.0
     */
    public String getFirstAvailableIP() {
        return networkArrayToString(getFirstAvailableIP_array());
    }

    /**
     * @return First available IP address as array
     * @since 1.0.0
     */
    public String[] getFirstAvailableIP_array() {
        return firstAvailableIP_a;
    }

    /**
     * @return Last available IP address
     * @since 1.0.0
     */
    public String getLastAvailableIP() {
        return networkArrayToString(getLastAvailableIP_array());
    }

    /**
     * @return Last available IP address as array
     * @since 1.0.0
     */
    public String[] getLastAvailableIP_array() {
        return lastAvailableIP_a;
    }

    /**
     * @return Broadcast IP address
     * @since 1.0.0
     */
    public String getBroadCastIP() {
        return networkArrayToString(getBroadCastIP_array());
    }

    /**
     * @return Broadcast IP address as array
     * @since 1.0.0
     */
    public String[] getBroadCastIP_array() {
        return broadCastIP_a;
    }

    /**
     * @return Class ID
     * @since 1.0.0
     */
    public String getClassID() {
        return networkArrayToString(getClassID_array());
    }

    /**
     * @return Class ID as array
     * @since 1.0.0
     */
    public String[] getClassID_array() {
        return classID_a;
    }

    /**
     * @return Class SNM
     * @since 1.0.0
     */
    public String getClassSNM() {
        return networkArrayToString(getClassSNM_array());
    }

    /**
     * @return Class SNM as array
     * @since 1.0.0
     */
    public String[] getClassSNM_array() {
        return classSNM_a;
    }

    /**
     * @return Character of the class
     * @since 1.0.0
     */
    public char getClassChar() {
        return classChar;
    }

    /**
     * @return Count of netbits
     * @since 1.0.0
     */
    public int getNetbits() {
        return netbits;
    }

    /**
     * @return Count of netbits with calculated number if negative
     * @since 1.0.0
     */
    public String getNetbitsString() {
        return netbits_s = netbits_s.trim();
    }

    /**
     * @return Count of subnetbits
     * @since 1.0.0
     */
    public int getSubnetbits() {
        return subnetbits;
    }

    /**
     * @return Count of subnetbits with calculated number if negative
     * @since 1.0.0
     */
    public String getSubnetbitsString() {
        return subnetbits_s = subnetbits_s.trim();
    }

    /**
     * @return Count of hostbits
     * @since 1.0.0
     */
    public int getHostbits() {
        return hostbits;
    }

    /**
     * @return Count of hostbits with calculated number if negative
     * @since 1.0.0
     */
    public String getHostbitsString() {
        return hostbits_s = hostbits_s.trim();
    }

    /**
     * @return Count of subnets
     * @since 1.0.0
     */
    public int getCountOfSubnets() {
        return countOfSubnets;
    }

    /**
     * @return Count of subnets with calculation
     * @since 1.0.0
     */
    public String getCountOfSubnets_calc() {
        return countOfSubnets_s;
    }

    /**
     * @return Count of hosts in a subnet
     * @since 1.0.0
     */
    public int getCountOfHosts() {
        return countOfHosts;
    }

    /**
     * @return Count of hosts in a subnet
     * @since 1.0.0
     */
    public String getCountOfHosts_calc() {
        return countOfHosts_s;
    }

    /**
     * check current network if it is super-netting
     *
     * @return if this subnet is super-netting
     * @since 1.0.0
     */
    public boolean isSupernetting() {
        if (Integer.parseInt(IP_a[0]) > 223) {// Class D & E; 224 and above: no supernet
            return false;
        } else if (Integer.parseInt(IP_a[0]) > 191) {// Class C: SUPERNETTING: only if 192-223
            if ((8 - getZeroCount()) < 0) {
                return true;
            }
        } else if (Integer.parseInt(IP_a[0]) > 127) {// Class B: ONLY if 128-191
            if ((16 - getZeroCount()) < 0) {
                return true;
            }
        } else {// Class A: ONLY if 0-127
            if ((24 - getZeroCount()) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * summarize current network with s
     *
     * @param s Subnet to add
     * @return the summarized network
     * @since 1.0.0
     */
    public Subnet summarize(Subnet s) {
        return summarize(this, s);
    }

    /**
     * summarize current network with subnet 2
     *
     * @param s1 current network
     * @param s2 Subnet to add
     * @return the summarized network
     * @since 1.0.0
     */
    public static Subnet summarize(Subnet s1, Subnet s2) {
        String erg_IP = "";
        String erg_SNM = "";
        if (s1.getIQ() == -1) {
            return null;
        } else if (s2.getIQ() == -1) {
            return null;
        }
        if (!s1.getIP_array()[0].equals(s2.getIP_array()[0])) {
            throw new IllegalArgumentException(IllegalArgument_FirstQuadIsNotTheSame);
        }

        String sa_IP1[] = new String[4];
        String sa_IP2[] = new String[4];
        String s_IP1;
        String s_IP2;
        for (int i = 0; i < 4; i++) {
            sa_IP1[i] = Integer.toBinaryString(Integer.parseInt(s1.getIP_array()[i]));
            sa_IP2[i] = Integer.toBinaryString(Integer.parseInt(s2.getIP_array()[i]));
            for (int ii = sa_IP1[i].length(); ii < 8; ii++) {
                sa_IP1[i] = "0" + sa_IP1[i];
            }
            for (int ii = sa_IP2[i].length(); ii < 8; ii++) {
                sa_IP2[i] = "0" + sa_IP2[i];
            }
        }
        s_IP1 = networkArrayToString(sa_IP1);
        s_IP2 = networkArrayToString(sa_IP2);

        if (s_IP1.length() != 35) {
            return null;
        }
        if (s_IP2.length() != 35) {
            return null;
        }


        // TEST: 192.168.12.3 /23
        for (int i = 35; i > 0; i--) {
            if (s_IP1.substring(0, i).equals(s_IP2.substring(0, i))) {
                String zwIpSub = s_IP1.substring(0, i);
                zwIpSub = zwIpSub.replace(".", "");
                int len = zwIpSub.length();
                for (int ii = zwIpSub.length(); ii < 33; ii++) {
                    zwIpSub += "0";
                }
                String zwIpSubF = "";
                for (int ii = 0; ii < 32; ii++) {
                    zwIpSubF += zwIpSub.charAt(ii);
                    if ((ii + 1) % 8 == 0) {
                        zwIpSubF += ".";
                    }
                    if (ii == 31) {
                        zwIpSub = zwIpSubF;
                    }
                }
                String[] zwIpSubA = zwIpSub.split("\\.");
                String[] ipS = new String[4];
                for (int ii = 0; ii < 4; ii++) {
                    ipS[ii] = Subnet.convertBinaryToDecimal(Long.parseLong(zwIpSubA[ii])) + "";
                }

                if (len == 32) {
                    len = 8;
                }
                String zwSNM = "";
                for (int ii = 0; ii < 32; ii++) {
                    if (ii < len) {
                        zwSNM += "1";
                    } else {
                        zwSNM += "0";
                    }
                    if ((ii + 1) % 8 == 0 && ii < 31) {
                        zwSNM += ".";
                    }
                }
                String[] zwSNMa = zwSNM.split("\\.");
                String[] snmS = new String[4];
                for (int ii = 0; ii < 4; ii++) {
                    snmS[ii] = Subnet.convertBinaryToDecimal(Long.parseLong(zwSNMa[ii])) + "";
                }

                erg_IP = networkArrayToString(ipS);
                erg_SNM = networkArrayToString(snmS);
                break;
            }
        }
        return new Subnet(erg_IP, erg_SNM);
    }

    private void calc() {
        // WILD SNM
        for (int i = 0; i < 4; i++) {
            WILD_a[i] = 255 - Integer.parseInt(SNM_a[i]) + "";
            // Magic Number
            setMagicNumber(i);
        }
        // SubnetID, firstAvailable, lastAvailable, Broadcast
        calc_addresses();

        // nets, subnets, host
        calc_bits();
    }

    private void calc_addresses() {
        // 'reset' | default values
        for (int i = 0; i < 4; i++) {
            if (Integer.parseInt(SNM_a[i]) == 255 && i != IQ) {
                subnetID_a[i] = IP_a[i];
                firstAvailableIP_a[i] = IP_a[i];
                lastAvailableIP_a[i] = IP_a[i];
                broadCastIP_a[i] = IP_a[i];
            }
            if (i == 3) {
                if (Integer.parseInt(SNM_a[2]) != 255 && Integer.parseInt(SNM_a[3]) != 255 && i != IQ) {// ==0?
                    subnetID_a[i] = 0 + "";
                    firstAvailableIP_a[i] = 1 + "";
                    lastAvailableIP_a[i] = 254 + "";
                    broadCastIP_a[i] = 255 + "";
                }
            }
        }

        if (IQ == 1) {
            subnetID_a[IQ] = MZ_min + "";
            firstAvailableIP_a[IQ] = MZ_min + "";
            lastAvailableIP_a[IQ] = MZ_max + "";
            broadCastIP_a[IQ] = MZ_max + "";
            broadCastIP_a[IQ + 1] = MZ_max + "";

            // +1
            subnetID_a[IQ + 1] = 0 + "";
            firstAvailableIP_a[IQ + 1] = 0 + "";
            lastAvailableIP_a[IQ + 1] = 255 + "";
            broadCastIP_a[IQ + 1] = 255 + "";
        } else if (IQ == 2) {
            subnetID_a[IQ] = MZ_min + "";
            firstAvailableIP_a[IQ] = MZ_min + "";
            lastAvailableIP_a[IQ] = MZ_max + "";
            broadCastIP_a[IQ] = MZ_max + "";
        } else if (IQ == 3) {
            subnetID_a[IQ] = MZ_min + "";
            if ((MZ_min + 1) < MZ_max) {
                firstAvailableIP_a[IQ] = (MZ_min + 1) + "";
            } else {
                firstAvailableIP_a[IQ] = MZ_min + "";
            }

            if ((MZ_max - 1) > 0) {
                lastAvailableIP_a[IQ] = (MZ_max - 1) + "";
            } else {
                lastAvailableIP_a[IQ] = MZ_max + "";
            }

            broadCastIP_a[IQ] = MZ_max + "";
        }
    }

    private void calc_bits() {
        // ClassNet
        for (int i = 1; i < 4; i++) {
            classID_a[i] = 0 + "";
            classSNM_a[i] = 0 + "";
        }

        // snm_allowed=0,128,192,224,240,248,252,254,255
        // KlassenGrenzen:/8,/16,/24

        countOfHosts = (int) (Math.pow(2, getZeroCount()) - 2);
        countOfHosts_s = "2^" + getZeroCount() + "-2 = " + (int) (Math.pow(2, getZeroCount()) - 2);

        if (Integer.parseInt(IP_a[0]) >= 0) {
            classID_a[0] = IP_a[0];
            classSNM_a[0] = 255 + "";
            hostbits = getZeroCount();
            hostbits_s = hostbits + "";
            if (Integer.parseInt(IP_a[0]) > 127) {
                classID_a[1] = IP_a[1];
                classSNM_a[1] = 255 + "";
                if (Integer.parseInt(IP_a[0]) > 191) {
                    classID_a[2] = IP_a[2];
                    classSNM_a[2] = 255 + "";
                    if (Integer.parseInt(IP_a[0]) > 223) {// Klasse D & E; ab 224
                        if (Integer.parseInt(IP_a[0]) > 223) {
                            classChar = 'D';
                        } else if (Integer.parseInt(IP_a[0]) > 239) {
                            classChar = 'E';
                        }

                        netbits = 32 - getZeroCount();
                        netbits_s = netbits + "";// ?
                        subnetbits = 0;
                        subnetbits_s = subnetbits + "";

                        countOfSubnets = (int) (Math.pow(2, 0));
                        countOfSubnets_s = "2^" + subnetbits + " = " + countOfSubnets;
                    } else {// SUPERNETTING : NUR wenn 192-223 (Klasse C)
                        classChar = 'C';
                        if ((8 - getZeroCount()) < 0) {
                            // supernetting = true;

                            netbits = 32 - getZeroCount();
                            netbits_s = netbits + " (24)";
                            subnetbits = 0;
                            subnetbits_s = subnetbits + " (" + (8 - getZeroCount()) + ")";
                        } else {
                            netbits = 24 - getZeroCount();
                            netbits_s = netbits + "";
                            subnetbits = 8 - getZeroCount();
                            subnetbits_s = subnetbits + "";
                        }

                        countOfSubnets = (int) (Math.pow(2, subnetbits));
                        countOfSubnets_s = "2^" + subnetbits + " = " + countOfSubnets;
                    }
                } else {// NUR wenn 128-191 (Klasse B)
                    classChar = 'B';
                    if ((16 - getZeroCount()) < 0) {
                        // supernetting=true;

                        netbits = 32 - getZeroCount();
                        netbits_s = netbits + " (16)";
                        subnetbits = 0;
                        subnetbits_s = subnetbits + " (" + (16 - getZeroCount()) + ")";
                    } else {
                        netbits = 16;
                        netbits_s = netbits + "";
                        subnetbits = 16 - getZeroCount();
                        subnetbits_s = subnetbits + "";
                    }

                    countOfSubnets = (int) (Math.pow(2, subnetbits));
                    countOfSubnets_s = "2^" + subnetbits + " = " + countOfSubnets;
                }
            } else {// NUR wenn 0-127 (Klasse A)
                classChar = 'A';
                if ((24 - getZeroCount()) < 0) {
                    // supernetting=true;

                    netbits = (32 - getZeroCount());
                    netbits_s = netbits + " (8)";
                    subnetbits = 0;
                    subnetbits_s = subnetbits + " (" + (24 - getZeroCount()) + ")";
                } else {
                    netbits = 8;
                    netbits_s = netbits + "";
                    subnetbits = 24 - getZeroCount();
                    subnetbits_s = subnetbits + "";
                }

                countOfSubnets = (int) (Math.pow(2, subnetbits));
                countOfSubnets_s = "2^" + (24 - getZeroCount()) + " = " + countOfSubnets;
            }
        }
    }

    /**
     * check if entry is valid
     *
     * @param s IP or Subnetmask
     * @return if entry exists
     */
    private boolean entryExists(String s) {
        String t = s.replace(".", "").replace("/", "");
        return !(t.equals(" ") || t.equals(""));
    }

    /**
     * add .0 if <b>s</b> isn't complete
     *
     * @param s IP or Subnetmask
     */
    private String addEnd0(String s) {
        int anzPoints = 0;
        while (s.contains("..")) {
            s = s.replace("..", ".");
        }
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                anzPoints++;
            }
        }
        for (int i = anzPoints; i < 3; i++) {
            s += ".0";
        }
        return s;
    }

    /**
     * tests if IP and Subnetmask is correct
     */
    private void checkEntry(String[] entry, boolean isIP) {
        final String where = isIP ? " [IP]" : " [SNM]";
        boolean pr_b = false;
        for (int i = 0; i < 4; i++) {
            if (entry[i].trim().equals("")) {
                throw new IllegalArgumentException(IllegalArgument_EntryMissing + where);
            }
            if (isIP) {
                if (!Test(entry[i]) || entry[i].contains("/")) {
                    throw new IllegalArgumentException(IllegalArgument_EntryNotSupported + where);
                }
                if (Integer.parseInt(entry[i]) > 255) {
                    throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + where);
                }
            } else {
                if (!Test(entry[i])) {
                    throw new IllegalArgumentException(IllegalArgument_EntryNotSupported + where);
                } else if (i == 0 && entry[0].charAt(0) == '/') {
                    pr_b = true;
                } else if (Integer.parseInt(entry[i]) > 100000000 || (!pr_b && Integer.parseInt(entry[i]) > 11111111)) {
                    throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + where);
                }

                // PrefixTest
                if (pr_b && i == 0) {
                    SNM_a = prefixTest(SNM_a);
                }

                // binärTest
                binarySNM(SNM_a, i);

                // Subnetmask check
                isSubnetOk(SNM_a, i);

                setMagicNumber(i);

                SNM = "";
                for (int k = 0; k < 4; k++) {
                    if (k < 3) {
                        SNM += SNM_a[k] + ".";
                    } else {
                        SNM += SNM_a[k];
                    }
                }
            }
        }
    }

    /**
     * convert Prefix to decimal subnetmask
     *
     * @param sa Subnetmask String Array
     * @return Converted subnetmask array
     */
    private String[] prefixTest(String[] sa) {
        int pr_length;
        String s_snm_pr = "";

        String pr_ss = sa[0].replace("/", "");

        if (!Test(pr_ss)) {
            throw new IllegalArgumentException(IllegalArgument_EntryNotSupported + " [SNM]");
        }
        if (pr_ss.trim().equals("")) {
            throw new IllegalArgumentException(IllegalArgument_EntryMissing + " [SNM]");
        }
        pr_length = Integer.parseInt(pr_ss);
        if (pr_length < 8) {
            throw new IllegalArgumentException(IllegalArgument_EntrySizeToSmall + " [SNM]");
        } else if (pr_length > 31) {
            throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + " [SNM]");
        }
        for (int j = 0; j < pr_length; j++) {
            s_snm_pr += "1";
            if ((j + 1) % 8 == 0) {
                s_snm_pr += ".";
            }
        }
        for (int j = pr_length; j < 32; j++) {
            s_snm_pr += "0";
            if ((j + 1) % 8 == 0 && (j + 1) != 32) {
                s_snm_pr += ".";
            }
        }
        return s_snm_pr.split("\\.");
    }

    /**
     * convert binary Subnetmask to decimal
     *
     * @param a_snm subnetmask String Array
     * @param i     current Array (max 3) index
     */
    private void binarySNM(String[] a_snm, int i) {
        if (a_snm[i].length() >= 4 && Test_B(Integer.parseInt(a_snm[i])) && Integer.parseInt(a_snm[i]) != 0) {
            if (8 > a_snm[i].length()) {
                String n = "0";
                int p = 8 - a_snm.length;
                for (int o = 0; o <= p; o++) {
                    n += "0";
                }
                a_snm[i] += n;
            }

            a_snm[i] = convertBinaryToDecimal(Long.parseLong(a_snm[i])) + "";

            if (Integer.parseInt(a_snm[i]) > 256) {// darf NIE true sein!
                throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + " [SNM]");
            }
        } else {// Binär:Fehler_l_ oder bereits richtiges Format
            if (!Test(a_snm[i])) {
                throw new IllegalArgumentException(IllegalArgument_EntryNotSupported);
            } else if (Integer.parseInt(a_snm[i]) > 256) {
                throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge);
            } else if (Test_B(Integer.parseInt(a_snm[i]))) {
                if (!a_snm[i].contains("1")) {
                    a_snm[i] = "0";// 8mal 0 ausbessern
                }
            }
        }
    }

    /**
     * test subnetmask
     *
     * @param a_snm Subnetmask String Array
     * @param i     actual array (max 3) index
     */
    private void isSubnetOk(String[] a_snm, int i) {
        boolean snm_allowed_b = false;
        for (int aSnm_erlaubt_int : snm_allowed_int) {
            if (Integer.parseInt(a_snm[i]) == aSnm_erlaubt_int) {
                snm_allowed_b = true;
            }
        }
        if (!snm_allowed_b) {
            throw new IllegalArgumentException(IllegalArgument_SubnetmaskContainsWrongNumber);
        }

        if (i != 3) {
            if (!(a_snm[i].equalsIgnoreCase(255 + "") || a_snm[i + 1].equalsIgnoreCase(0 + "") || a_snm[i + 1].equalsIgnoreCase("00000000"))) {
                boolean only0 = true;
                for (int j = 0; j < a_snm[i + 1].length(); j++) {
                    if (a_snm[i + 1].charAt(j) != '0') {
                        only0 = false;
                        break;
                    }
                }

                if (!only0) {
                    throw new IllegalArgumentException(IllegalArgument_Subnetmask255to0);
                }
            }
        }
    }

    /**
     * set MagicNumber
     *
     * @param i actual Array index (mind 1, max 3) [IQ]
     */
    private void setMagicNumber(int i) {
        if (Integer.parseInt(SNM_a[i]) != 255 && IQ == -1) {// Magische Zahl
            MZ = 256 - Integer.parseInt(SNM_a[i]);
            IQ = i;
            if (IQ == 0) {
                throw new IllegalArgumentException(IllegalArgument_SubnetmaskFirstQuadIsInteresting);
            }
        }

        if (IQ != -1) {
            for (int j = 0; j < 130; j++) {
                if ((MZ * j) <= Integer.parseInt(IP_a[IQ])) {
                    MZ_min = MZ * j;
                    MZ_max = MZ_min + MZ - 1;
                    if (MZ_max == -1) {
                        MZ_max = MZ - 1;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * counts zeros in snm
     *
     * @return zero count of snm
     */
    private int getZeroCount() {
        int zero_count = 0;
        for (int i = 3; i >= 0; i--) {
            if (Integer.parseInt(SNM_a[i]) == 0) {
                zero_count += 8;
            } else if (Integer.parseInt(SNM_a[i]) != 255) {
                String snmIQ = Integer.toBinaryString(Integer.parseInt(SNM_a[i])) + "";
                for (int j = (snmIQ.length() - 1); j >= 0; j--) {
                    if (snmIQ.charAt(j) == '0') {
                        zero_count += 1;
                    }
                }
            }
        }
        return zero_count;
    }

    /**
     * convert IP or SNM Array to String with '.' separator
     *
     * @param array Array
     * @return String
     */
    private static String networkArrayToString(String[] array) {
        String s = "";
        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1) {
                s += array[i] + ".";
            } else {
                s += array[i];
            }
        }
        return s;
    }

    /**
     * returns all Subnets in this network<br>
     * <i>just compare the network with the first available IP to check which subnet is the current</i>
     *
     * @return Set with all Subnets
     * @since 1.2.0
     */
    public Set<Subnet> getSubnets() {// see getAllNetworksFromTo
        Set<Subnet> subnets = new TreeSet<>();
        for (int iq_count = 0; iq_count <= 255; iq_count += getMagicNumber()) {
            switch (getIQ()) {
                case 1:
                    subnets.add(new Subnet(new String[]{getIP_array()[0] + "", iq_count + "", "0", "0"}, getSNM_array()));
                    break;
                case 2:
                    subnets.add(new Subnet(new String[]{getIP_array()[0] + "", getIP_array()[1] + "", iq_count + "", "0"}, getSNM_array()));
                    break;
                case 3:
                    subnets.add(new Subnet(new String[]{getIP_array()[0] + "", getIP_array()[1] + "", getIP_array()[2] + "", iq_count + ""}, getSNM_array()));
                    break;
            }
        }
        return subnets;
    }

    /**
     * returns Subnets from current network<br>
     * <b>NOTICE</b> it can take a while to get all
     *
     * @return Set with all Subnets
     * @since 1.2.0
     */
    public Set<Subnet> getAllSubnets() {
        return getAllSubnets(new Subnet(getFirstAvailableIP(), getSNM()), new Subnet(getLastAvailableIP(), getSNM()));
    }

    /**
     * returns Subnets from IP to IP<br>
     * Subnetmask is taken from first network
     *
     * @param from network with start IP
     * @param to   network with stop IP
     * @return Set with all Subnets between
     * @since 1.2.0
     */
    protected static Set<Subnet> getAllSubnets(Subnet from, Subnet to) {
        Set<Subnet> subnets = new TreeSet<>();
        for (int from0 = Integer.parseInt(from.getIP_array()[0]); from0 <= Integer.parseInt(to.getIP_array()[0]); from0++) {
            for (int from1 = Integer.parseInt(from.getIP_array()[1]); from1 <= Integer.parseInt(to.getIP_array()[1]); from1++) {
                for (int from2 = Integer.parseInt(from.getIP_array()[2]); from2 <= Integer.parseInt(to.getIP_array()[2]); from2++) {
                    for (int from3 = Integer.parseInt(from.getIP_array()[3]); from3 <= Integer.parseInt(to.getIP_array()[3]); from3++) {
                        subnets.add(new Subnet(new String[]{from0 + "", from1 + "", from2 + "", from3 + ""}, from.getSNM_array()));
                    }
                }
            }
        }
        return subnets;
    }

    /**
     * convert binary to decimal
     *
     * @param b binary number
     * @return decimal number
     * @since 1.0.0
     */
    public static long convertBinaryToDecimal(long b) {
        if (!Test_B(b)) {
            return errorNumber;
        }

        String erg;
        String zw = b + "";

        double m = 0;
        long e = 0;
        for (int i = zw.length() - 1; i >= 0; i--) {
            if (zw.charAt(i) == '1') {
                e += Math.pow(2.0, m);
            }
            m++;
        }
        erg = e + "";
        return Integer.parseInt(erg);
    }

    public static String[] integerArrayToStringArray(int[] ints) {
        return Arrays.toString(ints).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
    }

    // Test-s
    private static boolean Test(String text) {
        text = text.toLowerCase();
        return text.matches("[\\d./]*"); // \\\\ = \
    }

    private static boolean Test_B(long number) {
        boolean t = Test(number + "");
        if (t) {
            String numb = number + "";
            return numb.matches("[01]*");
        }
        return false;
    }

    /**
     * @param detailed complete output or only ip &amp; snm
     * @return IP and Subnetmask and other infos
     * @since 1.0.0
     */
    public String toString(boolean detailed) {
        if (!detailed) {
            return toString();
        }
        String supernetting = "";
        if (isSupernetting()) {
            supernetting = "\t\tsupernetting";
        }

        String s = "\nSubnet-INFO:\n";
        s += getIP() + "\t" + getSNM() + "\t(" + getWildmarkMask() + ")\tQuad: " + getIQ() + supernetting + "\n";
        s += "mz:" + getMagicNumber() + "\t\tmz:min:" + getMagicNumber_min() + "\tmz:max:" + getMagicNumber_max() + "\n";
        s += "subnet ID: \t" + getFirstAvailableIP() + "\nBroadcast: \t" + getLastAvailableIP() + "\n";
        s += "first available IP: \t" + getFirstAvailableIP() + "\nlast available IP: \t" + getLastAvailableIP() + "\n";
        s += "class: \t" + getClassChar() + "\nclass ID: \t" + getClassID() + "\nclass SNM: \t" + getClassSNM() + "\n";
        s += "netbits: " + getNetbitsString() + " \t\tsubnetbits: " + getSubnetbitsString() + " \thostbits: " + getHostbitsString() + "\n";
        s += "count of subnets: " + getCountOfSubnets_calc() + " \tcount of hosts: " + getCountOfHosts_calc();
        return s;
    }

    /**
     * @return IP and Subnetmask
     * @since 1.0.0
     */
    public String toString() {
        return IP + " " + SNM;
    }

    @Override
    public int compareTo(Subnet s) {
        for (int i = 0; i < 4; i++) {
            int ip = Integer.parseInt(getIP_array()[i]) - Integer.parseInt(s.getIP_array()[i]);
            if (ip != 0) {
                return ip;
            }
        }
        for (int i = 0; i < 4; i++) {
            int snm = Integer.parseInt(getSNM_array()[i]) - Integer.parseInt(s.getSNM_array()[i]);
            if (snm != 0) {
                return snm;
            }
        }
        return 0;
    }
}