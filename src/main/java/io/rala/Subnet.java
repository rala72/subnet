package io.rala;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.net.InterfaceAddress;
import java.util.*;

/**
 * IP address and Subnetmask needed to get a Subnet<br>
 *
 * <i>you should catch IllegalArgumentExceptions (see: {@link #EXCEPTION_MESSAGE})</i>
 *
 * @author rala<br>
 * <a href="mailto:code@rala.io">code@rala.io</a><br>
 * <a href="https://www.rala.io">www.rala.io</a>
 * @version 2.1.1
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public class Subnet implements Comparable<@NotNull Subnet>, Iterable<@NotNull Subnet> {
    //region error messages & valid snm entries
    /**
     * <b>initial text for known exceptions handled by this class</b>
     * <ul>
     * <li>if the IP address throws the error: message ends with {@link #EXCEPTION_MESSAGE_SUFFIX_IP}</li>
     * <li>if the Subnetmask throws the error: message ends with {@link #EXCEPTION_MESSAGE_SUFFIX_SNM}</li>
     * </ul>
     *
     * @see String#startsWith(String)
     * @see String#endsWith(String)
     */
    public static final String EXCEPTION_MESSAGE = "Subnet Error - ";
    public static final String EXCEPTION_MESSAGE_SUFFIX_IP = " [IP]";
    public static final String EXCEPTION_MESSAGE_SUFFIX_SNM = " [SNM]";
    public static final String ILLEGAL_ARGUMENT_ENTRY_MISSING =
        EXCEPTION_MESSAGE + "Entry missing - maybe the entry is \"\" or \" \"";
    public static final String ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED =
        EXCEPTION_MESSAGE + "Entry not supported / probably contains wrong characters";
    public static final String ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_SMALL =
        EXCEPTION_MESSAGE + "Size of the entry is to small";
    public static final String ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE =
        EXCEPTION_MESSAGE + "Size of the entry is to large";

    public static final String ILLEGAL_ARGUMENT_SUBNETMASK_FIRST_QUAD_IS_INTERESTING =
        EXCEPTION_MESSAGE + "Subnetmask: first quad is not allowed to be the interesting one"
            + EXCEPTION_MESSAGE_SUFFIX_SNM;
    public static final String ILLEGAL_ARGUMENT_SUBNETMASK_CONTAINS_WRONG_NUMBER =
        EXCEPTION_MESSAGE + "Subnetmask: contains wrong number"
            + EXCEPTION_MESSAGE_SUFFIX_SNM;
    public static final String ILLEGAL_ARGUMENT_SUBNETMASK_255_TO_0 =
        EXCEPTION_MESSAGE + "Subnetmask: unequal 255 -> next has to be 0"
            + EXCEPTION_MESSAGE_SUFFIX_SNM;
    public static final String ILLEGAL_ARGUMENT_FIRST_QUAD_IS_NOT_THE_SAME =
        EXCEPTION_MESSAGE + "Summarization: please make sure that both have the same 1. quad"
            + EXCEPTION_MESSAGE_SUFFIX_IP;

    private static final int[] SNM_ALLOWED = {0, 128, 192, 224, 240, 248, 252, 254, 255};
    // last Quad: 254+ no usable host (in general)
    //endregion

    //region subnet members
    private final int[] ipArray = new int[4];
    private final int[] snmArray = new int[4];
    private final int[] wildArray = new int[4];
    private int iq = -1;
    private int mz = -1;
    private int mzMin = -1;
    private int mzMax = -1;

    private final int[] subnetIdArray = new int[4];
    private final int[] firstAvailableIpArray = new int[4];
    private final int[] lastAvailableIpArray = new int[4];
    private final int[] broadCastIpArray = new int[4];
    private final int[] classIdArray = new int[4];
    private final int[] classSubnetmaskArray = new int[4];

    private char classChar = ' ';

    private int netbits;
    private String netbitsString = "";
    private int subnetbits;
    private String subnetbitsString = "";
    private int hostbits;
    private String hostbitsString = "";
    private int countOfSubnets;
    private String countOfSubnetsString = "";
    private int countOfHosts;
    private String countOfHostsString = "";
    //endregion

    //region constructors

    /**
     * generate a Subnet<br>
     * uses Subnetmask based on IP class
     *
     * @param ip IP address
     * @see #setSubnetmaskBasedOnClass()
     * @since 1.5.0
     */
    public Subnet(@NotNull String ip) {
        setIp(ip, false);
        setSubnetmaskBasedOnClass();
    }

    /**
     * generate a Subnet
     *
     * @param ip  IP address
     * @param snm Subnetmask
     * @since 1.0.0
     */
    public Subnet(@NotNull String ip, @NotNull String snm) {
        setIp(ip, false);
        setSubnetmask(snm);
    }

    /**
     * generate a Subnet<br>
     * uses Subnetmask based on IP class
     *
     * @param ip IP address
     * @see #setSubnetmaskBasedOnClass()
     * @since 1.5.0
     */
    public Subnet(@NotNull String[] ip) {
        setIp(ip, false);
        setSubnetmaskBasedOnClass();
    }

    /**
     * generate a Subnet with arrays
     *
     * @param ip  IP address
     * @param snm Subnetmask
     * @since 1.0.0
     */
    public Subnet(@NotNull String[] ip, @NotNull String[] snm) {
        setIp(ip, false);
        setSubnetmask(snm);
    }

    /**
     * generate a Subnet<br>
     * uses Subnetmask based on IP class
     *
     * @param ip IP address
     * @see #setSubnetmaskBasedOnClass()
     * @since 1.5.0
     */
    public Subnet(int[] ip) {
        setIp(ip, false);
        setSubnetmaskBasedOnClass();
    }

    /**
     * generate a Subnet with arrays
     *
     * @param ip  IP address
     * @param snm Subnetmask
     * @since 1.3.0
     */
    public Subnet(int[] ip, int[] snm) {
        setIp(ip, false);
        setSubnetmask(Arrays.toString(snm).replaceAll("[\\[\\]]", "").split("\\s*,\\s*"));
    }

    /**
     * generate a Subnet with {@link InterfaceAddress}
     *
     * @param address interfaceAddress
     * @since 2.0.1
     */
    public Subnet(@NotNull InterfaceAddress address) {
        this(address.getAddress().getHostAddress(), "/" + address.getNetworkPrefixLength());
    }
    //endregion

    //region setter

    /**
     * set IP address &amp; recalculate
     *
     * @param ip IP address
     * @since 1.0.0
     */
    public void setIp(@NotNull String ip) {
        setIp(ip, true);
    }

    /**
     * <p><b>
     * not suggested to set {@code recalculate} to {@code false}
     * </b></p>
     * set IP address
     *
     * @param ip          IP address
     * @param recalculate recalculate now or later..?
     * @see #setIp(String)
     * @see #recalculate()
     * @since 1.0.0
     * @deprecated use {@link #setIp(String)}
     */
    @Deprecated(since = "1.5.3", forRemoval = true)
    public void setIp(@NotNull String ip, boolean recalculate) {
        if (ip.isBlank())
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + EXCEPTION_MESSAGE_SUFFIX_IP);
        ip = clearAndAdd0(ip);

        // probably not very efficient; but recalculate shouldn't be used anyway
        if (recalculate && isSameSubnet(new Subnet(ip, getSubnetmask()))) recalculate = false;

        String[] stringArray = ip.split("\\.");
        checkEntryAndConvertSubnetmask(stringArray, true);
        for (int i = 0; i < stringArray.length; i++)
            ipArray[i] = Integer.parseInt(stringArray[i]);
        if (recalculate) setSubnetmask(getSubnetmask());
    }

    /**
     * set IP address &amp; recalculate
     *
     * @param ip IP address array
     * @since 1.0.0
     */
    public void setIp(@NotNull String[] ip) {
        setIp(convertNetworkArrayToString(ip));
    }

    /**
     * <p><b>
     * not suggested to set {@code recalculate} to {@code false}
     * </b></p>
     * set IP address
     *
     * @param ip          IP address
     * @param recalculate recalculate now or later..?
     * @see #setIp(String[])
     * @see #recalculate()
     * @since 1.0.0
     * @deprecated use {@link #setIp(String[])}
     */
    @Deprecated(since = "1.5.3", forRemoval = true)
    public void setIp(@NotNull String[] ip, boolean recalculate) {
        setIp(convertNetworkArrayToString(ip), recalculate);
    }

    /**
     * set IP address
     *
     * @param ip IP address array
     * @since 1.3.0
     */
    public void setIp(int[] ip) {
        setIp(convertNetworkArrayToString(ip));
    }

    /**
     * <p><b>
     * not suggested to set {@code recalculate} to {@code false}
     * </b></p>
     * set IP address
     *
     * @param ip          IP address
     * @param recalculate recalculate now or later..?
     * @see #setIp(int[])
     * @see #recalculate()
     * @since 1.3.0
     * @deprecated use {@link #setIp(int[])}
     */
    @Deprecated(since = "1.5.3", forRemoval = true)
    public void setIp(int[] ip, boolean recalculate) {
        setIp(convertNetworkArrayToString(ip), recalculate);
    }

    /**
     * set Subnetmask &amp; recalculate
     *
     * @param snm Subnetmask
     * @since 1.0.0
     */
    public void setSubnetmask(@NotNull String snm) {
        if (snm.isBlank())
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + EXCEPTION_MESSAGE_SUFFIX_SNM);
        snm = clearAndAdd0(snm);

        String[] stringArray = snm.split("\\.");
        checkEntryAndConvertSubnetmask(stringArray, false);
        for (int i = 0; i < stringArray.length; i++)
            snmArray[i] = Integer.parseInt(stringArray[i]);

        calc();
    }

    /**
     * set Subnetmask &amp; recalculate
     *
     * @param snm Subnetmask array
     * @since 1.0.0
     */
    public void setSubnetmask(@NotNull String[] snm) {
        setSubnetmask(convertNetworkArrayToString(snm));
    }

    /**
     * set Subnetmask &amp; recalculate
     *
     * @param snm Subnetmask array
     * @since 1.3.0
     */
    public void setSubnetmask(int[] snm) {
        setSubnetmask(convertNetworkArrayToString(snm));
    }

    /**
     * set Subnetmask based on class
     *
     * @since 1.5.3
     */
    public void setSubnetmaskBasedOnClass() {
        if (ipArray[0] < 128) setSubnetmask("/8");
        else if (ipArray[0] < 192) setSubnetmask("/16");
        else setSubnetmask("/24");
    }
    //endregion setter

    //region getter

    /**
     * @return IP address
     * @since 1.0.0
     */
    @NotNull
    public String getIp() {
        return convertNetworkArrayToString(getIpAsArray());
    }

    /**
     * @return IP address as array
     * @since 1.0.0
     */
    public int[] getIpAsArray() {
        return ipArray;
    }

    /**
     * @return Subnetmask
     * @since 1.0.0
     */
    @NotNull
    public String getSubnetmask() {
        return convertNetworkArrayToString(getSubnetmaskAsArray());
    }

    /**
     * @return Subnetmask as array
     * @since 1.0.0
     */
    public int[] getSubnetmaskAsArray() {
        return snmArray;
    }

    /**
     * @return WildmarkMask
     * @since 1.0.0
     */
    @NotNull
    public String getWildmarkMask() {
        return convertNetworkArrayToString(wildArray);
    }

    /**
     * @return WildmarkMask as array
     * @since 1.0.0
     */
    public int[] getWildmarkMaskAsArray() {
        return wildArray;
    }

    /**
     * @return IQ from array (0-3 NOT 1-4!!)
     * @since 1.0.0
     */
    public int getIq() {
        return iq;
    }

    /**
     * @return Magic Number
     * @since 1.0.0
     */
    public int getMagicNumber() {
        return mz;
    }

    /**
     * <b><i>use it only if you know what you do!</i></b>
     *
     * @return Magic Number - minimum
     * @since 1.0.0
     */
    public int getMagicNumberMin() {
        return mzMin;
    }

    /**
     * <b><i>use it only if you know what you do!</i></b>
     *
     * @return Magic Number - maximum
     * @since 1.0.0
     */
    public int getMagicNumberMax() {
        return mzMax;
    }

    /**
     * @return Subnet ID
     * @since 1.0.0
     */
    @NotNull
    public String getSubnetId() {
        return convertNetworkArrayToString(getSubnetIdAsArray());
    }

    /**
     * @return Subnet ID as array
     * @since 1.0.0
     */
    public int[] getSubnetIdAsArray() {
        return subnetIdArray;
    }

    /**
     * @return first available IP address
     * @since 1.0.0
     */
    @NotNull
    public String getFirstAvailableIp() {
        return convertNetworkArrayToString(getFirstAvailableIpAsArray());
    }

    /**
     * @return first available IP address as array
     * @since 1.0.0
     */
    public int[] getFirstAvailableIpAsArray() {
        return firstAvailableIpArray;
    }

    /**
     * @return last available IP address
     * @since 1.0.0
     */
    @NotNull
    public String getLastAvailableIp() {
        return convertNetworkArrayToString(getLastAvailableIpAsArray());
    }

    /**
     * @return last available IP address as array
     * @since 1.0.0
     */
    public int[] getLastAvailableIpAsArray() {
        return lastAvailableIpArray;
    }

    /**
     * @return Broadcast IP address
     * @since 1.0.0
     */
    @NotNull
    public String getBroadCastIp() {
        return convertNetworkArrayToString(getBroadCastIpAsArray());
    }

    /**
     * @return Broadcast IP address as array
     * @since 1.0.0
     */
    public int[] getBroadCastIpAsArray() {
        return broadCastIpArray;
    }

    /**
     * @return Class ID
     * @since 1.0.0
     */
    @NotNull
    public String getClassId() {
        return convertNetworkArrayToString(getClassIdAsArray());
    }

    /**
     * @return Class ID as array
     * @since 1.0.0
     */
    public int[] getClassIdAsArray() {
        return classIdArray;
    }

    /**
     * @return Class Subnetmask
     * @since 1.0.0
     */
    @NotNull
    public String getClassSubnetmask() {
        return convertNetworkArrayToString(getClassSubnetmaskAsArray());
    }

    /**
     * @return Class Subnetmask as array
     * @since 1.0.0
     */
    public int[] getClassSubnetmaskAsArray() {
        return classSubnetmaskArray;
    }

    /**
     * @return Character of the Class
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
     * @return Count of Netbits (with calculated number)
     * @since 1.0.0
     */
    @NotNull
    public String getNetbitsString() {
        return netbitsString = netbitsString.trim();
    }

    /**
     * @return Count of Subnetbits
     * @since 1.0.0
     */
    public int getSubnetbits() {
        return subnetbits;
    }

    /**
     * @return Count of Subnetbits (with calculated number)
     * @since 1.0.0
     */
    @NotNull
    public String getSubnetbitsString() {
        return subnetbitsString = subnetbitsString.trim();
    }

    /**
     * @return Count of Hostbits
     * @since 1.0.0
     */
    public int getHostbits() {
        return hostbits;
    }

    /**
     * @return Count of Hostbits (with calculated number)
     * @since 1.0.0
     */
    @NotNull
    public String getHostbitsString() {
        return hostbitsString = hostbitsString.trim();
    }

    /**
     * @return Count of Subnets
     * @since 1.0.0
     */
    public int getCountOfSubnets() {
        return countOfSubnets;
    }

    /**
     * @return Count of Subnets with calculation
     * @since 1.0.0
     */
    @NotNull
    public String getCountOfSubnetsCalc() {
        return countOfSubnetsString = countOfSubnetsString.trim();
    }

    /**
     * @return Count of Hosts in a Subnet
     * @since 1.0.0
     */
    public int getCountOfHosts() {
        return countOfHosts;
    }

    /**
     * @return Count of Hosts in a Subnet
     * @since 1.0.0
     */
    @NotNull
    public String getCountOfHostsCalc() {
        return countOfHostsString = countOfHostsString.trim();
    }

    /**
     * check current network if it is super-netting
     *
     * @return {@code true} if this Subnet is super-netting
     * @since 1.0.0
     */
    public boolean isSupernetting() {
        if (223 < ipArray[0]) // Class D & E; 224 and above: no supernet
            return false;
        else if (191 < ipArray[0]) // Class C: SUPERNETTING: only if 192-223
            return (8 - getZeroCount()) < 0;
        else if (127 < ipArray[0]) // Class B: ONLY if 128-191
            return (16 - getZeroCount()) < 0;
        else // Class A: ONLY if 0-127
            return (24 - getZeroCount()) < 0;
    }
    //endregion

    //region extras: summarize, subnets, contains

    /**
     * <p>summarize current network with other Subnet</p>
     * <p>if {@link #getIp()} is the same, the subnetmask is set to {@code '/30'}</p>
     *
     * @param s Subnet to summarize
     * @return the summarized network or {@code null} if any {@link #getIq()} returns {@code -1}
     * @throws IllegalArgumentException if first quad is not the same
     * @see #summarize(Subnet...)
     * @see #summarize(Collection)
     * @since 1.0.0
     */
    @Nullable
    public Subnet summarize(@NotNull Subnet s) {
        if (this.getIq() == -1 || s.getIq() == -1) return null;
        if (this.getIpAsArray()[0] != (s.getIpAsArray()[0]))
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_FIRST_QUAD_IS_NOT_THE_SAME);
        if (getIp().equals(s.getIp())) return new Subnet(getIp(), "/30");

        String[] ip1Array = new String[4];
        String[] ip2Array = new String[4];
        for (int i = 0; i < 4; i++) {
            ip1Array[i] = Integer.toBinaryString(this.getIpAsArray()[i]);
            ip2Array[i] = Integer.toBinaryString(s.getIpAsArray()[i]);
            for (int j = ip1Array[i].length(); j < 8; j++) ip1Array[i] = "0" + ip1Array[i];
            for (int j = ip2Array[i].length(); j < 8; j++) ip2Array[i] = "0" + ip2Array[i];
        }
        String ip1 = convertNetworkArrayToString(ip1Array);
        String ip2 = convertNetworkArrayToString(ip2Array);
        if (ip1.length() != 35 || ip2.length() != 35) return null;

        Subnet summarization = null;
        for (int i = 35; 0 < i; i--) {
            if (ip1.substring(0, i).equals(ip2.substring(0, i))) {
                String ipSub = ip1.substring(0, i).replace(".", "");
                int len = ipSub.length();
                StringBuilder ipSubBuilder = new StringBuilder(ipSub);
                ipSubBuilder.append("0".repeat(Math.max(0, 33 - ipSubBuilder.length())));
                ipSub = ipSubBuilder.toString();
                StringBuilder ipSubF = new StringBuilder();
                for (int j = 0; j < 32; j++) {
                    ipSubF.append(ipSub.charAt(j));
                    if ((j + 1) % 8 == 0) ipSubF.append(".");
                    if (j == 31) ipSub = ipSubF.toString();
                }
                String[] ip = convertBinaryNetworkAddressToDecimalStringArray(ipSub);

                if (len == 32) len = 30; // calculation should be 31 - but no usable address
                StringBuilder snmStringBuilder = new StringBuilder();
                for (int j = 0; j < 32; j++) {
                    if (j < len) snmStringBuilder.append("1");
                    else snmStringBuilder.append("0");
                    if ((j + 1) % 8 == 0 && j < 31) snmStringBuilder.append(".");
                }
                String[] snm = convertBinaryNetworkAddressToDecimalStringArray(snmStringBuilder.toString());

                summarization = new Subnet(ip, snm);
                break;
            }
        }
        return summarization;
    }

    /**
     * summarize current network with other Subnets
     *
     * @param s Subnets to summarize
     * @return the summarized network or {@code null}
     * @throws IllegalArgumentException if first quad is not the same
     * @see #summarize(Subnet)
     * @see #summarize(Collection)
     * @since 2.0.5
     */
    @Nullable
    public Subnet summarize(@NotNull Subnet... s) {
        Subnet summarized = this;
        for (Subnet subnet : s)
            if (this.getIpAsArray()[0] != subnet.getIpAsArray()[0])
                throw new IllegalArgumentException(ILLEGAL_ARGUMENT_FIRST_QUAD_IS_NOT_THE_SAME);
            else {
                summarized = summarized.summarize(subnet);
                if (summarized == null) return null;
            }
        return summarized;
    }

    /**
     * summarize current network with other Subnets
     *
     * @param s Subnets to summarize
     * @return the summarized network or {@code null}
     * @throws IllegalArgumentException if first quad is not the same
     * @see #summarize(Subnet)
     * @see #summarize(Subnet...)
     * @since 2.0.5
     */
    @Nullable
    public Subnet summarize(@NotNull Collection<@NotNull Subnet> s) {
        return summarize(s.toArray(new Subnet[0]));
    }

    /**
     * returns all Subnets in this network<br>
     * <i>just compare the network with the first available IP address to check which Subnet is the current</i>
     *
     * @return Set with all Subnets
     * @since 1.2.0
     */
    @NotNull
    @Unmodifiable
    public Set<@NotNull Subnet> getSubnets() {// see getSubnets(from, to)
        Set<Subnet> subnets = new TreeSet<>();
        for (int iqCount = 0; iqCount <= 255; iqCount += getMagicNumber()) {
            switch (getIq()) {
                case 1:
                    subnets.add(new Subnet(new int[]{getIpAsArray()[0], iqCount, 0, 0}, getSubnetmaskAsArray()));
                    break;
                case 2:
                    subnets.add(new Subnet(new int[]{getIpAsArray()[0], getIpAsArray()[1], iqCount, 0}, getSubnetmaskAsArray()));
                    break;
                case 3:
                    subnets.add(new Subnet(new int[]{getIpAsArray()[0], getIpAsArray()[1], getIpAsArray()[2], iqCount}, getSubnetmaskAsArray()));
                    break;
            }
        }
        return Collections.unmodifiableSet(subnets);
    }

    /**
     * returns Subnets from current network<br>
     * <b>NOTICE</b>: it can take a while to get all
     *
     * @return Set with all SubSubnets
     * @see #getSubnets(Subnet, Subnet)
     * @since 1.2.0
     */
    @NotNull
    @Unmodifiable
    public Set<@NotNull Subnet> getSubSubnets() {
        return getSubnets(new Subnet(getFirstAvailableIp(), getSubnetmask()), new Subnet(getLastAvailableIp(), getSubnetmask()));
    }

    /**
     * returns Subnets from <i>IP address</i> to <i>IP address</i><br>
     * <b>NOTICE</b>: it can take a while to get all<br>
     * Subnetmask is taken from first network
     *
     * @param from network with start IP address (included)
     * @param to   network with stop IP address (included)
     * @return all Subnets from to
     * @since 1.2.0
     */
    @NotNull
    @Unmodifiable
    protected static Set<@NotNull Subnet> getSubnets(@NotNull Subnet from, @NotNull Subnet to) {
        Set<Subnet> subnets = new TreeSet<>();
        for (int from0 = from.getIpAsArray()[0]; from0 <= to.getIpAsArray()[0]; from0++)
            for (int from1 = from.getIpAsArray()[1]; from1 <= to.getIpAsArray()[1]; from1++)
                for (int from2 = from.getIpAsArray()[2]; from2 <= to.getIpAsArray()[2]; from2++)
                    for (int from3 = from.getIpAsArray()[3]; from3 <= to.getIpAsArray()[3]; from3++)
                        subnets.add(new Subnet(new int[]{from0, from1, from2, from3}, from.getSubnetmaskAsArray()));
        return Collections.unmodifiableSet(subnets);
    }

    /**
     * @param s other Subnet
     * @return {@code true} if current Subnet is the same as other Subnet
     * @see #contains(Subnet)
     * @since 1.5.3
     */
    public boolean isSameSubnet(@NotNull Subnet s) {
        if (!isSameBeforeIq(s)) return false;
        for (int i = this.getIq(); i < 4; i++)
            if (this.getSubnetmaskAsArray()[i] != s.getSubnetmaskAsArray()[i]) return false;
        return isIpInIqInValidRange(s);
    }

    /**
     * @param s other Subnet
     * @return {@code true} if current Subnet contains other Subnet
     * @see #isSameSubnet(Subnet)
     * @since 1.4.0
     */
    public boolean contains(@NotNull Subnet s) {
        // missing: +-1
        if (!isSameBeforeIq(s)) return false;
        for (int i = this.getIq(); i < 4; i++) {
            if (this.getSubnetmaskAsArray()[i] < s.getSubnetmaskAsArray()[i]) break;
            else if (s.getSubnetmaskAsArray()[i] < this.getSubnetmaskAsArray()[i]) return false;
        }
        return isIpInIqInValidRange(s);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isSameBeforeIq(@NotNull Subnet s) {
        for (int i = 0; i < this.getIq(); i++) {
            if (this.getSubnetIdAsArray()[i] != s.getIpAsArray()[i]) return false;
            if (this.getSubnetmaskAsArray()[i] != s.getSubnetmaskAsArray()[i]) return false;
        }
        return true;
    }

    private boolean isIpInIqInValidRange(@NotNull Subnet s) {
        return this.getSubnetIdAsArray()[this.getIq()] <= s.getIpAsArray()[this.getIq()] &&
            s.getIpAsArray()[this.getIq()] <= this.getBroadCastIpAsArray()[this.getIq()];
    }
    //endregion

    //region calc

    /**
     * use this method <b>only</b> if you used a {@code setIp} method and
     * set {@code recalculate} to {@code false}<br>
     * recalculates everything
     *
     * @since 1.5.2
     * @deprecated use methods without the recalculate parameter
     */
    @Deprecated(since = "1.5.3", forRemoval = true)
    public void recalculate() {
        calc();
    }

    private void calc() {
        for (int i = 0; i < 4; i++) wildArray[i] = 255 - snmArray[i];
        setMagicNumber();
        calcAddresses();
        calcBits();
    }

    private void calcAddresses() {
        // 'reset' | default values
        for (int i = 0; i < 4; i++) {
            if (snmArray[i] == 255 && i != iq) {
                subnetIdArray[i] = ipArray[i];
                firstAvailableIpArray[i] = ipArray[i];
                lastAvailableIpArray[i] = ipArray[i];
                broadCastIpArray[i] = ipArray[i];
            }
            if (i == 3 && snmArray[2] != 255 && snmArray[3] != 255 && i != iq) {// ==0?
                subnetIdArray[i] = 0;
                firstAvailableIpArray[i] = 1;
                lastAvailableIpArray[i] = 254;
                broadCastIpArray[i] = 255;
            }
        }

        if (iq == 1) {
            subnetIdArray[iq] = mzMin;
            firstAvailableIpArray[iq] = mzMin;
            lastAvailableIpArray[iq] = mzMax;
            broadCastIpArray[iq] = mzMax;
            broadCastIpArray[iq + 1] = mzMax;
            subnetIdArray[iq + 1] = 0;
            firstAvailableIpArray[iq + 1] = 0;
            lastAvailableIpArray[iq + 1] = 255;
            broadCastIpArray[iq + 1] = 255;
        } else if (iq == 2) {
            subnetIdArray[iq] = mzMin;
            firstAvailableIpArray[iq] = mzMin;
            lastAvailableIpArray[iq] = mzMax;
            broadCastIpArray[iq] = mzMax;
        } else if (iq == 3) {
            subnetIdArray[iq] = mzMin;
            firstAvailableIpArray[iq] = mzMin + 1;
            lastAvailableIpArray[iq] = mzMax - 1;
            broadCastIpArray[iq] = mzMax;
        }
    }

    private void calcBits() {
        for (int i = 1; i < 4; i++)
            classIdArray[i] = classSubnetmaskArray[i] = 0;
        if (0 <= ipArray[0]) {
            classIdArray[0] = ipArray[0];
            classSubnetmaskArray[0] = 255;
            hostbits = getZeroCount();
            hostbitsString = String.valueOf(hostbits);
            if (127 < ipArray[0]) {
                classIdArray[1] = ipArray[1];
                classSubnetmaskArray[1] = 255;
                if (191 < ipArray[0]) {
                    classIdArray[2] = ipArray[2];
                    classSubnetmaskArray[2] = 255;
                    if (223 < ipArray[0]) {// Class D & E; 224+
                        if (239 < ipArray[0]) classChar = 'E';
                        else classChar = 'D';
                        netbits = 32 - getZeroCount();
                        netbitsString = String.valueOf(netbits);
                        subnetbits = 0;
                        subnetbitsString = String.valueOf(subnetbits);
                        countOfSubnets = (int) (Math.pow(2, 0));
                    } else {// SUPERNETTING : ONLY if 192-223 (Class C)
                        classChar = 'C';
                        if ((8 - getZeroCount()) < 0) {
                            netbits = 32 - getZeroCount();
                            netbitsString = netbits + " (24)";
                            subnetbits = 0;
                            subnetbitsString = subnetbits + " (" + (8 - getZeroCount()) + ")";
                        } else {
                            netbits = 24 - getZeroCount();
                            netbitsString = String.valueOf(netbits);
                            subnetbits = 8 - getZeroCount();
                            subnetbitsString = String.valueOf(subnetbits);
                        }
                        countOfSubnets = (int) (Math.pow(2, subnetbits));
                    }
                } else {// ONLY if 128-191 (Class B)
                    classChar = 'B';
                    if ((16 - getZeroCount()) < 0) {
                        netbits = 32 - getZeroCount();
                        netbitsString = netbits + " (16)";
                        subnetbits = 0;
                        subnetbitsString = subnetbits + " (" + (16 - getZeroCount()) + ")";
                    } else {
                        netbits = 16;
                        netbitsString = String.valueOf(netbits);
                        subnetbits = 16 - getZeroCount();
                        subnetbitsString = String.valueOf(subnetbits);
                    }
                    countOfSubnets = (int) (Math.pow(2, subnetbits));
                }
            } else {// ONLY if 0-127 (Class A)
                classChar = 'A';
                netbits = 8;
                netbitsString = String.valueOf(netbits);
                subnetbits = 24 - getZeroCount();
                subnetbitsString = String.valueOf(subnetbits);
                countOfSubnets = (int) (Math.pow(2, subnetbits));
            }
            countOfSubnetsString = "2^" + subnetbits + " = " + countOfSubnets;
        }
        countOfHosts = (int) (Math.pow(2, getZeroCount()) - 2);
        countOfHostsString = "2^" + getZeroCount() + "-2 = " + countOfHosts;
    }
    //endregion

    //region valid checks and other internal methods

    /**
     * add .0 if {@code s} isn't complete
     *
     * @param s IP address or Subnetmask
     */
    @NotNull
    private String clearAndAdd0(@NotNull String s) {
        while (s.contains("..")) s = s.replace("..", ".");
        if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        int countDots = 0;
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '.') countDots++;
        return s + ".0".repeat(Math.max(0, 3 - countDots));
    }

    /**
     * tests if IP address and Subnetmask is correct<br>
     * and converts Subnetmask to correct decimal format
     */
    private void checkEntryAndConvertSubnetmask(@NotNull String[] entry, boolean isIp) {
        boolean isPrefix = entry[0].charAt(0) == '/';
        for (int i = 0; i < 4; i++) {
            if (isIp) {
                if (!testNumber(entry[i]) || entry[i].contains("/"))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED + EXCEPTION_MESSAGE_SUFFIX_IP);
                if (255 < Integer.parseInt(entry[i]))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + EXCEPTION_MESSAGE_SUFFIX_IP);
            } else {
                boolean lastQuad = i == 3;
                if (!testNumber(entry[i]))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_NOT_SUPPORTED + EXCEPTION_MESSAGE_SUFFIX_SNM);
                else if (!isPrefix && (11111111 < Integer.parseInt(entry[i]) || (lastQuad && 11111100 < Integer.parseInt(entry[i]))))
                    throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + EXCEPTION_MESSAGE_SUFFIX_SNM);
                if (isPrefix && i == 0) {
                    String[] stringArray = convertPrefixAndValidate(entry);
                    System.arraycopy(stringArray, 0, entry, 0, entry.length);
                }
                entry[i] = String.valueOf(convertBinarySubnetmaskToDecimal(Integer.parseInt(entry[i]), lastQuad));
                isSubnetOk(convertStringArrayToIntegerArray(entry), i);
            }
        }
    }

    /**
     * convert Prefix to binary Subnetmask
     *
     * @param snm Subnetmask String array
     * @return converted Subnetmask array
     */
    @NotNull
    private String[] convertPrefixAndValidate(@NotNull String[] snm) {
        String prefix = snm[0].replace("/", "");
        if (prefix.isBlank())
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_MISSING + EXCEPTION_MESSAGE_SUFFIX_SNM);

        int prefixLength = Integer.parseInt(prefix);
        if (prefixLength < 8)
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_SMALL + EXCEPTION_MESSAGE_SUFFIX_SNM);
        else if (30 < prefixLength)
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + EXCEPTION_MESSAGE_SUFFIX_SNM);
        StringBuilder prefixSnm = new StringBuilder();
        for (int j = 0; j < prefixLength; j++) {
            prefixSnm.append("1");
            if ((j + 1) % 8 == 0) prefixSnm.append(".");
        }
        for (int j = prefixLength; j < 32; j++) {
            prefixSnm.append("0");
            if ((j + 1) % 8 == 0 && (j + 1) != 32) prefixSnm.append(".");
        }
        return prefixSnm.toString().split("\\.");
    }

    /**
     * convert binary Subnetmask to decimal
     *
     * @param snm Subnetmask part
     * @return new Subnetmask part
     */
    private int convertBinarySubnetmaskToDecimal(int snm, boolean lastQuad) {
        if (3 < String.valueOf(snm).length() && testBinary(snm) && snm != 0) {
            // fill zeros to 8
            while (String.valueOf(snm).length() < 8) snm *= 10;
            snm = (int) convertBinaryToDecimal(snm);
        } else {
            if (255 < snm || (lastQuad && 252 < snm))
                throw new IllegalArgumentException(ILLEGAL_ARGUMENT_ENTRY_SIZE_TO_LARGE + EXCEPTION_MESSAGE_SUFFIX_SNM);
            else if (testBinary(snm) && !String.valueOf(snm).contains("1")) snm = 0;
        }
        return snm;
    }

    /**
     * testNumber Subnetmask
     *
     * @param snm Subnetmask String array
     * @param i   actual array (max 3) index
     */
    private void isSubnetOk(int[] snm, int i) {
        boolean isSnmAllowed = false;
        for (int snmAllowed : SNM_ALLOWED)
            if (snm[i] == snmAllowed) {
                isSnmAllowed = true;
                break;
            }
        if (!isSnmAllowed)
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_SUBNETMASK_CONTAINS_WRONG_NUMBER);

        if (i != 3 && snm[i] != 255 && snm[i + 1] != 0)
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_SUBNETMASK_255_TO_0);
    }

    /**
     * finds first iq quad and sets magic number
     */
    private void setMagicNumber() {
        int quad = -1;
        for (int i = 0; i < snmArray.length; i++)
            if (snmArray[i] != 255) {
                quad = i;
                break;
            }

        if (snmArray[quad] != 255) {
            mz = 256 - snmArray[quad];
            iq = quad;
            if (iq == 0)
                throw new IllegalArgumentException(ILLEGAL_ARGUMENT_SUBNETMASK_FIRST_QUAD_IS_INTERESTING);
        }

        if (iq != -1)
            for (int j = 0; j < 130; j++)
                if ((mz * j) <= ipArray[iq]) {
                    mzMin = mz * j;
                    mzMax = mzMin + mz - 1;
                    if (mzMax == -1) mzMax = mz - 1;
                } else break;
    }

    /**
     * counts zeros in Subnetmask
     *
     * @return zero count of Subnetmask
     */
    private int getZeroCount() {
        int zeroCount = 0;
        for (int i = 3; 0 <= i; i--)
            if (snmArray[i] == 0) zeroCount += 8;
            else if (snmArray[i] != 255) {
                String snmIq = Integer.toBinaryString(snmArray[i]);
                for (int j = (snmIq.length() - 1); 0 <= j; j--)
                    if (snmIq.charAt(j) == '0') zeroCount += 1;
            }
        return zeroCount;
    }
    //endregion

    //region convert...

    /**
     * converts array to String with '.' separator
     *
     * @param array array
     * @return String
     */
    @NotNull
    private static String convertNetworkArrayToString(@NotNull String[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1) stringBuilder.append(array[i]).append(".");
            else stringBuilder.append(array[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * converts array to String with '.' separator
     *
     * @param array array
     * @return String
     */
    @NotNull
    private static String convertNetworkArrayToString(int[] array) {
        return convertNetworkArrayToString(convertIntegerArrayToStringArray(array));
    }

    /**
     * converts binary address to decimal array
     *
     * @param string address
     * @return decimal array
     */
    @NotNull
    private static String[] convertBinaryNetworkAddressToDecimalStringArray(@NotNull String string) {
        String[] split = string.split("\\.");
        String[] address = new String[4];
        for (int j = 0; j < 4; j++)
            address[j] = String.valueOf(Subnet.convertBinaryToDecimal(split[j]));
        return address;
    }

    /**
     * convert binary to decimal
     *
     * @param b binary number
     * @return decimal number
     * @see Long#parseLong(String, int)
     * @see #convertBinaryToDecimal(long)
     * @since 2.0.3
     */
    public static long convertBinaryToDecimal(@NotNull String b) {
        return Long.parseLong(b, 2);
    }

    /**
     * convert binary to decimal
     *
     * @param b binary number
     * @return decimal number
     * @see Long#parseLong(String, int)
     * @see #convertBinaryToDecimal(String)
     * @since 1.0.0
     */
    public static long convertBinaryToDecimal(long b) {
        return convertBinaryToDecimal(String.valueOf(b));
    }

    /**
     * convert decimal to binary
     *
     * @param d decimal number
     * @return binary number
     * @see Long#toBinaryString(long)
     * @see #convertDecimalToBinary(long)
     * @since 2.0.3
     */
    public static long convertDecimalToBinary(@NotNull String d) {
        return convertDecimalToBinary(Long.parseLong(d));
    }

    /**
     * convert decimal to binary
     *
     * @param d decimal number
     * @return binary number
     * @see Long#toBinaryString(long)
     * @see #convertDecimalToBinary(String)
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
    @NotNull
    public static String[] convertIntegerArrayToStringArray(int[] ints) {
        return Arrays.toString(ints).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
    }

    /**
     * @param strings {@link String} array
     * @return int array
     * @since 1.4.0
     */
    public static int[] convertStringArrayToIntegerArray(@NotNull String[] strings) {
        int[] intArray = new int[strings.length];
        for (int i = 0; i < strings.length; i++) intArray[i] = Integer.parseInt(strings[i]);
        return intArray;
    }
    //endregion

    //region testNumber, testBinary

    /**
     * tests if text is valid number
     *
     * @param text text to check
     * @return {@code true} if valid number
     */
    private static boolean testNumber(String text) {
        return text.toLowerCase().matches("/?[\\d.]*");
    }

    /**
     * tests if number is valid binary number
     *
     * @param number number to check
     * @return {@code true} if valid binary number
     */
    private static boolean testBinary(long number) {
        return testNumber(String.valueOf(number)) && String.valueOf(number).matches("[01]*");
    }
    //endregion

    //region toString, compareTo, ...

    /**
     * @param detailed complete output or only IP address &amp; Subnetmask
     * @return IP and Subnetmask and (optional) other information
     * @since 1.0.0
     */
    @NotNull
    public String toString(boolean detailed) {
        if (!detailed) return toString();
        String s = Subnet.class.getSimpleName() + "-INFO:\n";

        int offset = -15;
        s += formatString(getIp(), offset) + " ";
        s += formatString(getSubnetmask(), offset) + " ";
        s += formatString("(" + getWildmarkMask() + ")", offset - 2L) + " ";
        s += "Quad: " + getIq();
        s += isSupernetting() ? " " + formatString("supernetting", 3L + 15) : "";
        s += "\n";
        s += formatString("mz:", 3) + " " + formatString(String.valueOf(getMagicNumber()), -11) + " ";
        s += formatString("mz:min:", 7) + " " + formatString(String.valueOf(getMagicNumberMin()), -7) + " ";
        s += formatString("mz:max:", 7) + " " + getMagicNumberMax();
        s += "\n";

        s += formatString("subnet ID:", offset) + " " + getSubnetId();
        s += "\n";
        s += formatString("broadcast:", offset) + " " + getBroadCastIp();
        s += "\n";
        offset = -20;
        s += formatString("first available IP:", offset) + " " + getFirstAvailableIp();
        s += "\n";
        s += formatString("last available IP:", offset) + " " + getLastAvailableIp();
        s += "\n";

        offset = -15;
        s += formatString("class:", offset) + " " + getClassChar();
        s += "\n";
        s += formatString("class ID:", offset) + " " + getClassId();
        s += "\n";
        s += formatString("class SNM:", offset) + " " + getClassSubnetmask();
        s += "\n";
        s += formatString("netbits:", offset) + " " + formatString(getNetbitsString(), offset) + " ";
        s += formatString("subnetbits:", offset) + " " + formatString(getSubnetbitsString(), offset) + " ";
        s += formatString("hostbits:", offset) + " " + getHostbitsString();
        s += "\n";

        offset = -20;
        s += formatString("count of subnets:", offset) + " " + formatString(getCountOfSubnetsCalc(), offset / 2) + " ";
        s += formatString("count of hosts:", offset) + " " + getCountOfHostsCalc();
        return s;
    }

    /**
     * @return IP address and Subnetmask
     * @since 1.0.0
     */
    @NotNull
    public String toString() {
        return getIp() + " " + getSubnetmask();
    }

    /**
     * offset negative: text on start<br>
     * offset positive: text on end
     *
     * @param string string to format
     * @param offset offset after/before text
     * @return formatted string
     * @since 2.0.0
     */
    @NotNull
    protected static String formatString(@Nullable String string, long offset) {
        return String.format("%1$" + offset + "s", string);
    }

    /**
     * @param s other Subnet
     * @return difference between IP addresses and if equal Subnetmask
     * @since 1.1.0
     */
    @Override
    public int compareTo(@NotNull Subnet s) {
        for (int i = 0; i < 4; i++) {
            int ip = getIpAsArray()[i] - s.getIpAsArray()[i];
            if (ip != 0) return ip;
        }
        for (int i = 0; i < 4; i++) {
            int snm = getSubnetmaskAsArray()[i] - s.getSubnetmaskAsArray()[i];
            if (snm != 0) return snm;
        }
        return 0;
    }

    /**
     * @return a copy of current Subnet based on IP address and SNM
     * @since 1.5.3
     */
    @NotNull
    public Subnet copy() {
        return new Subnet(getIp(), getSubnetmask());
    }

    /**
     * @param o has to be Subnet
     * @return {@code true} if IP address and Subnetmask are equal
     * @since 1.4.0
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Subnet && compareTo((Subnet) o) == 0;
    }

    /**
     * like {@link #equals(Object)} but ignoring the IP address<br>
     * can check if it takes any affect except on the IP if you used a setIp method and set recalculate to false
     *
     * @param o    has to be Subnet
     * @param deep if deep is {@code false} {@link #equals(Object)} is used
     * @return {@code true} if everything except the IP address is equal
     * @see #equals(Object)
     * @since 1.5.3
     */
    public boolean equals(Object o, boolean deep) {
        if (!deep) return equals(o);
        else if (!(o instanceof Subnet)) return false;
        Subnet subnet = (Subnet) o;
        if (!this.getSubnetmask().equals(subnet.getSubnetmask())) return false;
        // other values not required to check:
        return this.getSubnetId().equals(subnet.getSubnetId());
    }

    /**
     * @return hashCode is product of hashCodes from {@link #getIp()} and {@link #getSubnetmask()}
     * @since 1.5.4
     */
    @Override
    public int hashCode() {
        return getIp().hashCode() * getSubnetmask().hashCode();
    }

    /**
     * @return Iterator to go over all subnets in same network
     * @see #getSubnets()
     * @since 1.5.4
     */
    @Override
    @NotNull
    public Iterator<@NotNull Subnet> iterator() {
        return getSubnets().iterator();
    }
    //endregion
}
