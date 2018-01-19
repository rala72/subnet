// ralaVersion 1.2.0
// Methods from Subnettable 1.2.6 [origin: 1.1.8]
package at.rala;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

// mini bug: toString: " \t" between -> tab removed
// possible TO DO [onHold]: inner classes for IP and SNM

/**
 * IP-Address and Subnetmask needed to get a Subnet<br>
 * <br>
 * this tool is in use from rala for his program: "Subnettable" (German: "Subnetztabelle")<br>
 * <p>
 * <i>you should catch IllegalArgumentExceptions</i>
 *
 * @author rala<br>
 * <a href="mailto:code@rala.io">code@rala.io</a><br>
 * <a href="www.rala.io">www.rala.io</a>
 * @version 1.5.1
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Subnet implements Comparable<Subnet> {
    //region error messages & valid snm entries
    /**
     * <p>
     * easy to check with <i>{@link String#startsWith(String)}</i> and <i>{@link String#endsWith(String)}</i>
     * </p>
     * <p>
     * if the IP throws the error: message ends with "[IP]"<br>
     * if the SNM throws the error: message ends with "[SNM]"
     * </p>
     */
    public static final String EXCEPTION_MESSAGE = "Subnet Error - ";
    public static final String ILLEGAL_ARGUMENT_ENTRY_MISSING = EXCEPTION_MESSAGE + "Entry missing - maybe the entry is \"\" or \" \"";
    public static final String ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED = EXCEPTION_MESSAGE + "Entry not supported / probably contains wrong characters: check it again!";
    public static final String ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_SMALL = EXCEPTION_MESSAGE + "Size of the entry is to small";
    public static final String ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE = EXCEPTION_MESSAGE + "Size of the entry is to large";
    private static final String ERROR_IP = " [IP]";
    private static final String ERROR_SNM = " [SNM]";

    public static final String ILLEGAL_ARGUMENT_SUBNETMASK_FIRST_QUAD_IS_INTERESTING = "First Quad is not allowed to be the interesting one";
    public static final String ILLEGAL_ARGUMENT_SUBNETMASK_CONTAINS_WRONG_NUMBER = "Subnetmask contains wrong number";
    public static final String ILLEGAL_ARGUMENT_SUBNETMASK_255_TO_0 = "Subnetmask: unequal 255 -> next has to be 0";

    public static final String ILLEGAL_ARGUMENT_FIRST_QUAD_IS_NOT_THE_SAME = EXCEPTION_MESSAGE + "Summarize Exception: please make sure that both have the same Network (1. Quad)";

    private static final int[] SNM_ALLOWED = {0, 128, 192, 224, 240, 248, 252, 254, 255};
    // last Quad: 254+ no usable host (in general)
    //endregion

    //region subnet members
    private final int[] IP_a = new int[4];
    private final int[] SNM_a = new int[4];
    private final int[] WILD_a = new int[4];
    private int IQ = -1;
    private int MZ = -1;
    private int MZ_min = -1;
    private int MZ_max = -1;

    private final int[] subnetID_a = new int[4];
    private final int[] firstAvailableIP_a = new int[4];
    private final int[] lastAvailableIP_a = new int[4];
    private final int[] broadCastIP_a = new int[4];
    private final int[] classID_a = new int[4];
    private final int[] classSNM_a = new int[4];

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
    //endregion

    //region constructors

    /**
     * generate a Subnet<br>
     * uses Subnetmask based on ip Class
     *
     * @param ip ip-Address
     * @since 1.5.0
     */
    public Subnet(String ip) {
        setIP(ip, false);
        if (IP_a[0] < 128) setSNM("/8");
        else if (IP_a[0] < 192) setSNM("/16");
        else setSNM("/24");
    }

    /**
     * generate a Subnet
     *
     * @param ip  ip-Address
     * @param snm Subnetmask
     * @since 1.0.0
     */
    public Subnet(String ip, String snm) {
        setIP(ip, false);
        setSNM(snm);
    }

    /**
     * generate a Subnet<br>
     * uses Subnetmask based on ip Class
     *
     * @param ip ip-Address
     * @since 1.5.0
     */
    public Subnet(String[] ip) {
        setIP(ip, false);
        if (IP_a[0] < 128) setSNM("/8");
        else if (IP_a[0] < 192) setSNM("/16");
        else setSNM("/24");
    }

    /**
     * generate a Subnet with Arrays
     *
     * @param ip  ip-Address [4]
     * @param snm Subnetmask [4]
     * @since 1.0.0
     */
    public Subnet(String[] ip, String[] snm) {
        setIP(ip, false);
        setSNM(snm);
    }

    /**
     * generate a Subnet<br>
     * uses Subnetmask based on ip Class
     *
     * @param ip ip-Address
     * @since 1.5.0
     */
    public Subnet(int[] ip) {
        setIP(ip, false);
        if (IP_a[0] < 128) setSNM("/8");
        else if (IP_a[0] < 192) setSNM("/16");
        else setSNM("/24");
    }

    /**
     * generate a Subnet with Arrays
     *
     * @param ip  ip-Address [4]
     * @param snm Subnetmask [4]
     * @since 1.3.0
     */
    public Subnet(int[] ip, int[] snm) {
        setIP(ip, false);
        setSNM(Arrays.toString(snm).replaceAll("[\\[\\]]", "").split("\\s*,\\s*"));
    }
    //endregion

    //region setter

    /**
     * set IP to <i>ip</i><br>
     * and recalculate
     *
     * @param ip IP-Address
     * @since 1.0.0
     */
    public void setIP(String ip) {
        setIP(ip, true);
    }

    /**
     * set IP to <i>ip</i>
     *
     * @param ip          IP-Address
     * @param reCalculate recalculate now or later..?
     * @since 1.0.0
     */
    public void setIP(String ip, boolean reCalculate) {
        if (ip.trim().equals("")) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + ERROR_IP);
        ip = clearAndAdd0(ip);

        String[] stringArray = ip.split("\\.");
        checkEntryAndConvertSnm(stringArray, true);
        for (int i = 0; i < stringArray.length; i++) IP_a[i] = Integer.parseInt(stringArray[i]);
        if (reCalculate) setSNM(getSNM());
    }

    /**
     * set IP-Address
     *
     * @param ip_a IP-Address Array [4]
     * @since 1.0.0
     */
    public void setIP(String[] ip_a) {
        setIP(convertNetworkArrayToString(ip_a));
    }

    /**
     * set IP to <i>ip</i>
     *
     * @param ip_a        IP-Address
     * @param reCalculate recalculate now or later..?
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.0.0
     */
    public void setIP(String[] ip_a, boolean reCalculate) {
        String ip_s = convertNetworkArrayToString(ip_a);
        setIP(ip_s, reCalculate);
    }

    /**
     * set IP-Address
     *
     * @param ip_a IP-Address Array [4]
     * @since 1.3.0
     */
    public void setIP(int[] ip_a) {
        setIP(convertNetworkArrayToString(ip_a));
    }

    /**
     * set IP to <i>ip</i>
     *
     * @param ip_a        IP-Address
     * @param reCalculate recalculate now or later..?
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.3.0
     */
    public void setIP(int[] ip_a, boolean reCalculate) {
        String ip_s = convertNetworkArrayToString(ip_a);
        setIP(ip_s, reCalculate);
    }

    /**
     * set subnetmask &amp; recalculate table NOW<br>
     *
     * @param snm subnetmask
     * @since 1.0.0
     */
    public void setSNM(String snm) {
        if (snm.trim().equals("")) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + ERROR_SNM);
        snm = clearAndAdd0(snm);

        String[] stringArray = snm.split("\\.");
        checkEntryAndConvertSnm(stringArray, false);
        for (int i = 0; i < stringArray.length; i++) SNM_a[i] = Integer.parseInt(stringArray[i]);

        calc();
    }

    /**
     * set subnetmask &amp; recalculate table NOW<br>
     *
     * @param snm_a subnetmask Array [4]
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.0.0
     */
    public void setSNM(String[] snm_a) {
        setSNM(convertNetworkArrayToString(snm_a));
    }

    /**
     * set subnetmask &amp; recalculate table NOW<br>
     *
     * @param snm_a subnetmask Array [4]
     * @throws IllegalArgumentException if there are wrong parameters
     * @since 1.3.0
     */
    public void setSNM(int[] snm_a) {
        setSNM(convertNetworkArrayToString(snm_a));
    }
    //endregion setter

    //region getter (basic)

    /**
     * @return IP-Address
     * @since 1.0.0
     */
    public String getIP() {
        return convertNetworkArrayToString(getIP_array());
    }

    /**
     * @return IP-Address as array
     * @since 1.0.0
     */
    public int[] getIP_array() {
        return IP_a;
    }

    /**
     * @return subnetmask
     * @see Subnet#getSubnetmask()
     * @since 1.0.0
     */
    public String getSNM() {
        return convertNetworkArrayToString(getSNM_array());
    }

    /**
     * @return subnetmask
     * @see Subnet#getSNM()
     * @since 1.0.0
     */
    public String getSubnetmask() {
        return getSNM();
    }

    /**
     * @return subnetmask as array
     * @see Subnet#getSubnetmask_array()
     * @since 1.0.0
     */
    public int[] getSNM_array() {
        return SNM_a;
    }

    /**
     * @return subnetmask as array
     * @see Subnet#getSNM_array()
     * @since 1.0.0
     */
    public int[] getSubnetmask_array() {
        return SNM_a;
    }

    /**
     * @return WildmarkMask
     * @since 1.0.0
     */
    public String getWildmarkMask() {
        return convertNetworkArrayToString(WILD_a);
    }

    /**
     * @return WildmarkMask as array
     * @since 1.0.0
     */
    public int[] getWildmarkMask_array() {
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
        return convertNetworkArrayToString(getSubnetID_array());
    }

    /**
     * @return Subnet ID as array
     * @since 1.0.0
     */
    public int[] getSubnetID_array() {
        return subnetID_a;
    }

    /**
     * @return First available IP address
     * @since 1.0.0
     */
    public String getFirstAvailableIP() {
        return convertNetworkArrayToString(getFirstAvailableIP_array());
    }

    /**
     * @return First available IP address as array
     * @since 1.0.0
     */
    public int[] getFirstAvailableIP_array() {
        return firstAvailableIP_a;
    }

    /**
     * @return Last available IP address
     * @since 1.0.0
     */
    public String getLastAvailableIP() {
        return convertNetworkArrayToString(getLastAvailableIP_array());
    }

    /**
     * @return Last available IP address as array
     * @since 1.0.0
     */
    public int[] getLastAvailableIP_array() {
        return lastAvailableIP_a;
    }

    /**
     * @return Broadcast IP address
     * @since 1.0.0
     */
    public String getBroadCastIP() {
        return convertNetworkArrayToString(getBroadCastIP_array());
    }

    /**
     * @return Broadcast IP address as array
     * @since 1.0.0
     */
    public int[] getBroadCastIP_array() {
        return broadCastIP_a;
    }

    /**
     * @return Class ID
     * @since 1.0.0
     */
    public String getClassID() {
        return convertNetworkArrayToString(getClassID_array());
    }

    /**
     * @return Class ID as array
     * @since 1.0.0
     */
    public int[] getClassID_array() {
        return classID_a;
    }

    /**
     * @return Class SNM
     * @since 1.0.0
     */
    public String getClassSNM() {
        return convertNetworkArrayToString(getClassSNM_array());
    }

    /**
     * @return Class SNM as array
     * @since 1.0.0
     */
    public int[] getClassSNM_array() {
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
        return countOfSubnets_s = countOfSubnets_s.trim();
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
        return countOfHosts_s = countOfHosts_s.trim();
    }

    /**
     * check current network if it is super-netting
     *
     * @return if this subnet is super-netting
     * @since 1.0.0
     */
    public boolean isSupernetting() {
        if (IP_a[0] > 223) {// Class D & E; 224 and above: no supernet
            return false;
        } else if (IP_a[0] > 191) {// Class C: SUPERNETTING: only if 192-223
            return (8 - getZeroCount()) < 0;
        } else if (IP_a[0] > 127) {// Class B: ONLY if 128-191
            return (16 - getZeroCount()) < 0;
        } else {// Class A: ONLY if 0-127
            return (24 - getZeroCount()) < 0;
        }
    }
    //endregion

    //region extras: summarize, subnets, contains

    /**
     * summarize current network with other subnet
     *
     * @param s Subnet to add
     * @return the summarized network
     * @since 1.0.0
     */
    public Subnet summarize(Subnet s) {
        if (this.getIQ() == -1) return null;
        else if (s.getIQ() == -1) return null;
        if (!(this.getIP_array()[0] == (s.getIP_array()[0]))) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_FIRST_QUAD_IS_NOT_THE_SAME);

        String sa_IP1[] = new String[4];
        String sa_IP2[] = new String[4];
        String s_IP1;
        String s_IP2;
        for (int i = 0; i < 4; i++) {
            sa_IP1[i] = Integer.toBinaryString(this.getIP_array()[i]);
            sa_IP2[i] = Integer.toBinaryString(s.getIP_array()[i]);
            for (int j = sa_IP1[i].length(); j < 8; j++) sa_IP1[i] = "0" + sa_IP1[i];
            for (int j = sa_IP2[i].length(); j < 8; j++) sa_IP2[i] = "0" + sa_IP2[i];
        }
        s_IP1 = convertNetworkArrayToString(sa_IP1);
        s_IP2 = convertNetworkArrayToString(sa_IP2);

        if (s_IP1.length() != 35 || s_IP2.length() != 35) return null;

        for (int i = 35; i > 0; i--) {
            if (s_IP1.substring(0, i).equals(s_IP2.substring(0, i))) {
                String zwIpSub = s_IP1.substring(0, i);
                zwIpSub = zwIpSub.replace(".", "");
                int len = zwIpSub.length();
                StringBuilder zwIpSubBuilder = new StringBuilder(zwIpSub);
                for (int j = zwIpSubBuilder.length(); j < 33; j++) zwIpSubBuilder.append("0");
                zwIpSub = zwIpSubBuilder.toString();
                StringBuilder zwIpSubF = new StringBuilder();
                for (int j = 0; j < 32; j++) {
                    zwIpSubF.append(zwIpSub.charAt(j));
                    if ((j + 1) % 8 == 0) zwIpSubF.append(".");
                    if (j == 31) zwIpSub = zwIpSubF.toString();
                }
                String[] zwIpSubA = zwIpSub.split("\\.");
                String[] ipS = new String[4];
                for (int j = 0; j < 4; j++) ipS[j] = String.valueOf(Subnet.convertBinaryToDecimal(Long.parseLong(zwIpSubA[j])));

                if (len == 32) len = 8;
                StringBuilder zwSNM = new StringBuilder();
                for (int j = 0; j < 32; j++) {
                    if (j < len) zwSNM.append("1");
                    else zwSNM.append("0");
                    if ((j + 1) % 8 == 0 && j < 31) zwSNM.append(".");
                }
                String[] zwSNMa = zwSNM.toString().split("\\.");
                String[] snmS = new String[4];
                for (int j = 0; j < 4; j++) snmS[j] = String.valueOf(Subnet.convertBinaryToDecimal(Long.parseLong(zwSNMa[j])));

                return new Subnet(convertNetworkArrayToString(ipS), convertNetworkArrayToString(snmS));
            }
        }
        return null; // should not happen
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
                    subnets.add(new Subnet(new int[]{getIP_array()[0], iq_count, 0, 0}, getSNM_array()));
                    break;
                case 2:
                    subnets.add(new Subnet(new int[]{getIP_array()[0], getIP_array()[1], iq_count, 0}, getSNM_array()));
                    break;
                case 3:
                    subnets.add(new Subnet(new int[]{getIP_array()[0], getIP_array()[1], getIP_array()[2], iq_count}, getSNM_array()));
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
    public Set<Subnet> getSubSubnets() {
        return getSubnets(new Subnet(getFirstAvailableIP(), getSNM()), new Subnet(getLastAvailableIP(), getSNM()));
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
    protected static Set<Subnet> getSubnets(Subnet from, Subnet to) {
        Set<Subnet> subnets = new TreeSet<>();
        for (int from0 = from.getIP_array()[0]; from0 <= to.getIP_array()[0]; from0++)
            for (int from1 = from.getIP_array()[1]; from1 <= to.getIP_array()[1]; from1++)
                for (int from2 = from.getIP_array()[2]; from2 <= to.getIP_array()[2]; from2++)
                    for (int from3 = from.getIP_array()[3]; from3 <= to.getIP_array()[3]; from3++)
                        subnets.add(new Subnet(new int[]{from0, from1, from2, from3}, from.getSNM_array()));
        return subnets;
    }

    /**
     * @param s other subnet
     * @return if current subnet contains other subnet
     * @since 1.4.0
     */
    public boolean contains(Subnet s) {
        // missing: +-1
        for (int i = 0; i < this.getIQ(); i++) {
            if (!(this.getSubnetID_array()[i] == s.getIP_array()[i])) return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!(this.getSNM_array()[i] <= s.getSNM_array()[i])) return false;
        }
        return
            this.getSubnetID_array()[this.getIQ()] <= s.getIP_array()[this.getIQ()] &&
                this.getBroadCastIP_array()[this.getIQ()] >= s.getIP_array()[this.getIQ()];
    }
    //endregion

    //region calc
    private void calc() {
        // WILD SNM
        for (int i = 0; i < 4; i++) WILD_a[i] = 255 - SNM_a[i];

        // Magic Number
        setMagicNumber();

        // SubnetID, firstAvailable, lastAvailable, Broadcast
        calc_addresses();

        // nets, subnets, host
        calc_bits();
    }

    private void calc_addresses() {
        // 'reset' | default values
        for (int i = 0; i < 4; i++) {
            if (SNM_a[i] == 255 && i != IQ) {
                subnetID_a[i] = IP_a[i];
                firstAvailableIP_a[i] = IP_a[i];
                lastAvailableIP_a[i] = IP_a[i];
                broadCastIP_a[i] = IP_a[i];
            }
            if (i == 3) {
                if (SNM_a[2] != 255 && SNM_a[3] != 255 && i != IQ) {// ==0?
                    subnetID_a[i] = 0;
                    firstAvailableIP_a[i] = 1;
                    lastAvailableIP_a[i] = 254;
                    broadCastIP_a[i] = 255;
                }
            }
        }

        if (IQ == 1) {
            subnetID_a[IQ] = MZ_min;
            firstAvailableIP_a[IQ] = MZ_min;
            lastAvailableIP_a[IQ] = MZ_max;
            broadCastIP_a[IQ] = MZ_max;
            broadCastIP_a[IQ + 1] = MZ_max;
            // +1
            subnetID_a[IQ + 1] = 0;
            firstAvailableIP_a[IQ + 1] = 0;
            lastAvailableIP_a[IQ + 1] = 255;
            broadCastIP_a[IQ + 1] = 255;
        } else if (IQ == 2) {
            subnetID_a[IQ] = MZ_min;
            firstAvailableIP_a[IQ] = MZ_min;
            lastAvailableIP_a[IQ] = MZ_max;
            broadCastIP_a[IQ] = MZ_max;
        } else if (IQ == 3) {
            subnetID_a[IQ] = MZ_min;
            if ((MZ_min + 1) < MZ_max) firstAvailableIP_a[IQ] = (MZ_min + 1);
            else firstAvailableIP_a[IQ] = MZ_min;
            if ((MZ_max - 1) > 0) lastAvailableIP_a[IQ] = (MZ_max - 1);
            else lastAvailableIP_a[IQ] = MZ_max;
            broadCastIP_a[IQ] = MZ_max;
        }
    }

    private void calc_bits() {
        // ClassNet
        for (int i = 1; i < 4; i++) {
            classID_a[i] = 0;
            classSNM_a[i] = 0;
        }
        countOfHosts = (int) (Math.pow(2, getZeroCount()) - 2);
        countOfHosts_s = "2^" + getZeroCount() + "-2 = " + (int) (Math.pow(2, getZeroCount()) - 2);
        if (IP_a[0] >= 0) {
            classID_a[0] = IP_a[0];
            classSNM_a[0] = 255;
            hostbits = getZeroCount();
            hostbits_s = String.valueOf(hostbits);
            if (IP_a[0] > 127) {
                classID_a[1] = IP_a[1];
                classSNM_a[1] = 255;
                if (IP_a[0] > 191) {
                    classID_a[2] = IP_a[2];
                    classSNM_a[2] = 255;
                    if (IP_a[0] > 223) {// Class D & E; 224+
                        if (IP_a[0] > 239) classChar = 'E';
                        else classChar = 'E';
                        netbits = 32 - getZeroCount();
                        netbits_s = String.valueOf(netbits);// ?
                        subnetbits = 0;
                        subnetbits_s = String.valueOf(subnetbits);
                        countOfSubnets = (int) (Math.pow(2, 0));
                        countOfSubnets_s = "2^" + subnetbits + " = " + countOfSubnets;
                    } else {// SUPERNETTING : ONLY if 192-223 (Class C)
                        classChar = 'C';
                        if ((8 - getZeroCount()) < 0) {
                            // supernetting = true;
                            netbits = 32 - getZeroCount();
                            netbits_s = netbits + " (24)";
                            subnetbits = 0;
                            subnetbits_s = subnetbits + " (" + (8 - getZeroCount()) + ")";
                        } else {
                            netbits = 24 - getZeroCount();
                            netbits_s = String.valueOf(netbits);
                            subnetbits = 8 - getZeroCount();
                            subnetbits_s = String.valueOf(subnetbits);
                        }
                        countOfSubnets = (int) (Math.pow(2, subnetbits));
                        countOfSubnets_s = "2^" + subnetbits + " = " + countOfSubnets;
                    }
                } else {// ONLY if 128-191 (Class B)
                    classChar = 'B';
                    if ((16 - getZeroCount()) < 0) {
                        // supernetting=true;
                        netbits = 32 - getZeroCount();
                        netbits_s = netbits + " (16)";
                        subnetbits = 0;
                        subnetbits_s = subnetbits + " (" + (16 - getZeroCount()) + ")";
                    } else {
                        netbits = 16;
                        netbits_s = String.valueOf(netbits);
                        subnetbits = 16 - getZeroCount();
                        subnetbits_s = String.valueOf(subnetbits);
                    }
                    countOfSubnets = (int) (Math.pow(2, subnetbits));
                    countOfSubnets_s = "2^" + subnetbits + " = " + countOfSubnets;
                }
            } else {// ONLY if 0-127 (Class A)
                classChar = 'A';
                if ((24 - getZeroCount()) < 0) {
                    // supernetting=true;
                    netbits = 32 - getZeroCount();
                    netbits_s = netbits + " (8)";
                    subnetbits = 0;
                    subnetbits_s = subnetbits + " (" + (24 - getZeroCount()) + ")";
                } else {
                    netbits = 8;
                    netbits_s = String.valueOf(netbits);
                    subnetbits = 24 - getZeroCount();
                    subnetbits_s = String.valueOf(subnetbits);
                }
                countOfSubnets = (int) (Math.pow(2, subnetbits));
                countOfSubnets_s = "2^" + (24 - getZeroCount()) + " = " + countOfSubnets;
            }
        }
    }
    //endregion

    //region valid checks and other internal methods

    /**
     * check if entry is valid
     *
     * @param s IP or Subnetmask
     * @return if entry exists
     */
    private boolean entryExists(String s) {
        return !s.replace(".", "").replace("/", "").trim().isEmpty();
    }

    /**
     * add .0 if <b>s</b> isn't complete
     *
     * @param s IP or Subnetmask
     */
    private String clearAndAdd0(String s) {
        int anzPoints = 0;
        while (s.contains("..")) s = s.replace("..", ".");
        if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '.') anzPoints++;
        StringBuilder sBuilder = new StringBuilder(s);
        for (int i = anzPoints; i < 3; i++) sBuilder.append(".0");
        s = sBuilder.toString();
        return s;
    }

    /**
     * tests if IP and Subnetmask is correct<br>
     * and converts snm to correct decimal format
     */
    private void checkEntryAndConvertSnm(String[] entry, boolean isIP) {
        boolean isPrefix = entry[0].charAt(0) == '/';
        for (int i = 0; i < 4; i++) {
            if (entry[i].trim().equals(""))
                throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + (isIP ? ERROR_IP : ERROR_SNM));
            if (isIP) {
                if (!testNumber(entry[i]) || entry[i].contains("/"))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED + ERROR_IP);
                if (Integer.parseInt(entry[i]) > 255)
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + ERROR_IP);
            } else {
                boolean lastQuad = i == 3;
                if (!testNumber(entry[i]))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED + ERROR_SNM);
                else if (!isPrefix && (Integer.parseInt(entry[i]) > 11111111 || (lastQuad && Integer.parseInt(entry[i]) > 11111100)))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + ERROR_SNM);
                if (isPrefix && i == 0) {
                    String[] stringArray = convertPrefixAndValidate(entry);
                    System.arraycopy(stringArray, 0, entry, 0, entry.length);
                }
                entry[i] = String.valueOf(convertBinarySnmToDecimal(Integer.parseInt(entry[i]), lastQuad));
                // SubnetmaskCheck
                isSubnetOk(convertStringArrayToIntegerArray(entry), i);
            }
        }
    }

    /**
     * convert Prefix to binary subnetmask
     *
     * @param snm_a Subnetmask String Array
     * @return Converted subnetmask array
     */
    private String[] convertPrefixAndValidate(String[] snm_a) {
        int pr_length;
        StringBuilder s_snm_pr = new StringBuilder();

        String pr_ss = snm_a[0].replace("/", "");

        if (!testNumber(pr_ss)) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED + ERROR_SNM);
        if (pr_ss.trim().equals("")) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + ERROR_SNM);
        pr_length = Integer.parseInt(pr_ss);
        if (pr_length < 8) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_SMALL + ERROR_SNM);
        else if (pr_length > 30) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + ERROR_SNM);
        for (int j = 0; j < pr_length; j++) {
            s_snm_pr.append("1");
            if ((j + 1) % 8 == 0) s_snm_pr.append(".");
        }
        for (int j = pr_length; j < 32; j++) {
            s_snm_pr.append("0");
            if ((j + 1) % 8 == 0 && (j + 1) != 32) s_snm_pr.append(".");
        }
        return s_snm_pr.toString().split("\\.");
    }

    /**
     * convert binary Subnetmask to decimal
     *
     * @param snm subnetmask part
     * @return new snm part
     */
    private int convertBinarySnmToDecimal(int snm, boolean lastQuad) {
        if (String.valueOf(snm).length() >= 4 && testBinary(snm) && snm != 0) {
            // fill zeros to 8
            while (String.valueOf(snm).length() < 8) snm *= 10;
            snm = (int) convertBinaryToDecimal(snm);
            // must NEVER be true!
            if (snm > 255) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + ERROR_SNM);
        } else {
            if (!testNumber(String.valueOf(snm))) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED);
            else if (snm > 255 || (lastQuad && snm > 252)) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + ERROR_SNM);
            else if (testBinary(snm)) {
                // 8 times 0 becomes only one
                if (!String.valueOf(snm).contains("1")) snm = 0;
            }
        }
        return snm;
    }

    /**
     * testNumber subnetmask
     *
     * @param a_snm Subnetmask String Array
     * @param i     actual array (max 3) index
     */
    private void isSubnetOk(int[] a_snm, int i) {
        boolean snm_allowed_b = false;
        for (int snm_allowed_int_ : SNM_ALLOWED)
            if (a_snm[i] == snm_allowed_int_) snm_allowed_b = true;
        if (!snm_allowed_b) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_SUBNETMASK_CONTAINS_WRONG_NUMBER);

        if (i != 3) {
            if (!(a_snm[i] == 255 || a_snm[i + 1] == 0 || a_snm[i + 1] == 0)) {
                boolean only0 = true;
                for (int j = 0; j < String.valueOf(a_snm[i + 1]).length(); j++) {
                    if (String.valueOf(a_snm[i + 1]).charAt(j) != '0') {
                        only0 = false;
                        break;
                    }
                }
                if (!only0) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_SUBNETMASK_255_TO_0);
            }
        }
    }

    /**
     * finds first IQ quad and sets magic number
     */
    private void setMagicNumber() {
        int quad = -1;
        for (int i = 0; i < SNM_a.length; i++) {
            if (SNM_a[i] != 255) {
                quad = i;
                break;
            }
        }

        if (SNM_a[quad] != 255) {// Magic Number
            MZ = 256 - SNM_a[quad];
            IQ = quad;
            if (IQ == 0) throw new IllegalArgumentException(ILLEGAL_ARGUMENT_SUBNETMASK_FIRST_QUAD_IS_INTERESTING);
        }

        if (IQ != -1) {
            for (int j = 0; j < 130; j++) {
                if ((MZ * j) <= IP_a[IQ]) {
                    MZ_min = MZ * j;
                    MZ_max = MZ_min + MZ - 1;
                    if (MZ_max == -1) MZ_max = MZ - 1;
                } else break;
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
            if (SNM_a[i] == 0) zero_count += 8;
            else if (SNM_a[i] != 255) {
                String snmIQ = Integer.toBinaryString(SNM_a[i]);
                for (int j = (snmIQ.length() - 1); j >= 0; j--)
                    if (snmIQ.charAt(j) == '0') zero_count += 1;
            }
        }
        return zero_count;
    }
    //endregion

    //region convert...

    /**
     * convert IP or SNM Array to String with '.' separator
     *
     * @param array Array
     * @return String
     */
    private static String convertNetworkArrayToString(String[] array) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1) s.append(array[i]).append(".");
            else s.append(array[i]);
        }
        return s.toString();
    }

    /**
     * convert IP or SNM Array to String with '.' separator
     *
     * @param array Array
     * @return String
     */
    private static String convertNetworkArrayToString(int[] array) {
        return convertNetworkArrayToString(convertIntegerArrayToStringArray(array));
    }

    /**
     * convert binary to decimal
     *
     * @param b binary number
     * @return decimal number
     * @see Long#parseLong(String, int)
     * @since 1.0.0
     */
    public static long convertBinaryToDecimal(long b) {
        return Long.parseLong(String.valueOf(b), 2);
    }

    /**
     * convert decimal to binary
     *
     * @param d decimal number
     * @return binary number
     * @see Long#toBinaryString(long)
     * @since 1.4.0
     */
    public static long convertDecimalToBinary(long d) {
        return Long.parseLong(Long.toBinaryString(d));
    }

    /**
     * @param ints int array
     * @return string array
     * @since 1.1.0
     */
    public static String[] convertIntegerArrayToStringArray(int[] ints) {
        return Arrays.toString(ints).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
    }

    /**
     * @param strings {@link String} array
     * @return int array
     * @since 1.4.0
     */
    public static int[] convertStringArrayToIntegerArray(String[] strings) {
        int[] intArray = new int[strings.length];
        for (int i = 0; i < strings.length; i++) intArray[i] = Integer.parseInt(strings[i]);
        return intArray;
    }
    //endregion

    //region testNumber, testBinary

    /**
     * tests if {@link String} is valid number
     *
     * @param text text to check
     * @return if number
     */
    private static boolean testNumber(String text) {
        return text.toLowerCase().matches("[\\d./]*");
    }

    /**
     * tests if number is valid binary number
     *
     * @param number number to check
     * @return if binary number
     */
    private static boolean testBinary(long number) {
        return testNumber(String.valueOf(number)) && String.valueOf(number).matches("[01]*");
    }
    //endregion

    //region toString, compareTo, equals

    /**
     * @param detailed complete output or only ip &amp; snm
     * @return IP and Subnetmask and other information
     * @since 1.0.0
     */
    public String toString(boolean detailed) {
        if (!detailed) return toString();
        String supernetting = "";
        if (isSupernetting()) supernetting = "\t\tsupernetting";

        String s = "\nSubnet-INFO:\n";
        s += getIP() + "\t" + getSNM() + "\t(" + getWildmarkMask() + ")\tQuad: " + getIQ() + supernetting + "\n";
        s += "mz:" + getMagicNumber() + "\t\tmz:min:" + getMagicNumber_min() + "\tmz:max:" + getMagicNumber_max() + "\n";
        s += "subnet ID: \t" + getSubnetID() + "\nBroadcast: \t" + getBroadCastIP() + "\n";
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
        return getIP() + " " + getSNM();
    }

    /**
     * @param s other subnet
     * @return difference between ip and if equal snm
     * @since 1.1.0
     */
    @Override
    public int compareTo(Subnet s) {
        for (int i = 0; i < 4; i++) {
            int ip = getIP_array()[i] - s.getIP_array()[i];
            if (ip != 0) return ip;
        }
        for (int i = 0; i < 4; i++) {
            int snm = getSNM_array()[i] - s.getSNM_array()[i];
            if (snm != 0) return snm;
        }
        return 0;
    }

    /**
     * @param obj has to be subnet
     * @return if subnet ip and snm are equal
     * @since 1.4.0
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Subnet && compareTo((Subnet) obj) == 0;
    }
    //endregion
}