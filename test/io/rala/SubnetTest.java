package io.rala;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("FieldCanBeLocal")
public class SubnetTest {
    //region default config
    // maybe make more methods for same method with different cases
    private static final String NOT_CORRECT = "not correct";
    private static final String IP_NOT_CORRECT = "IP is " + NOT_CORRECT;
    private static final String SNM_NOT_CORRECT = "SNM is " + NOT_CORRECT;
    private static boolean printAll = true;
    private static boolean printAllDetailed = false;
    //endregion

    private Subnet subnet1;
    private Subnet subnet2;
    private Subnet subnet3;

    @Before
    public void setUp() {
        subnet1 = new Subnet("192.168.50.20", "255.255.240.0");
        subnet2 = new Subnet("192.168.50", "255.255.11100000.0");
        subnet3 = new Subnet("10", "255.255");
        if (printAll) {
            printAll = false;
            // System.out.println(new Subnet("192.168.12.3", "/23").toString(printAllDetailed));
            System.out.println(subnet1.toString(printAllDetailed));
            System.out.println(subnet2.toString(printAllDetailed));
            System.out.println(subnet3.toString(printAllDetailed));
        }
    }

    //region setter
    @Test
    @SuppressWarnings("deprecation")
    public void setIP() {
        subnet1.setIp("10");
        subnet2.setIp("10", false);
        subnet3.setIp(new String[]{"10"});
        assert subnet1.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
        assert subnet2.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
        assert subnet3.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
        subnet1.setIp(new String[]{"15", "0", "0", "0"}, false);
        subnet2.setIp(new int[]{15, 0, 0, 0});
        subnet3.setIp(new int[]{15, 0, 0, 0}, false);
        assert subnet1.getIp().equals("15.0.0.0") : IP_NOT_CORRECT;
        assert subnet2.getIp().equals("15.0.0.0") : IP_NOT_CORRECT;
        assert subnet3.getIp().equals("15.0.0.0") : IP_NOT_CORRECT;
    }

    @Test
    public void setSNM() {
        subnet1.setSnm("255");
        subnet2.setSnm(new String[]{"255"});
        subnet3.setSnm(new int[]{255});
        assert subnet1.getSnm().equals("255.0.0.0") : SNM_NOT_CORRECT;
        assert subnet2.getSnm().equals("255.0.0.0") : SNM_NOT_CORRECT;
        assert subnet3.getSnm().equals("255.0.0.0") : SNM_NOT_CORRECT;
    }
    //endregion

    //region getter (basic)
    @Test
    public void getIP() {
        assert subnet1.getIp().equals("192.168.50.20") : IP_NOT_CORRECT;
        assert subnet2.getIp().equals("192.168.50.0") : IP_NOT_CORRECT;
        assert subnet3.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
    }

    @Test
    public void getIP_array() {
        assert Arrays.equals(subnet1.getIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "50", "20"})) : IP_NOT_CORRECT;
        assert Arrays.equals(subnet2.getIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "50", "0"})) : IP_NOT_CORRECT;
        assert Arrays.equals(subnet3.getIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"10", "0", "0", "0"})) : IP_NOT_CORRECT;
    }

    @Test
    public void getSNM() { // includes also getSubnetmask()
        assert subnet1.getSnm().equals("255.255.240.0") : SNM_NOT_CORRECT;
        assert subnet2.getSnm().equals("255.255.224.0") : SNM_NOT_CORRECT;
        assert subnet3.getSnm().equals("255.255.0.0") : SNM_NOT_CORRECT;
    }

    @Test
    public void getSNM_array() {
        assert Arrays.equals(subnet1.getSnmAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "240", "0"})) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet2.getSnmAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "224", "0"})) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet3.getSnmAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "0", "0"})) : SNM_NOT_CORRECT;
    }

    @Test
    public void getSubnetmask_array() {
        assert Arrays.equals(subnet1.getSubnetmaskAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "240", "0"})) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet2.getSubnetmaskAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "224", "0"})) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet3.getSubnetmaskAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "0", "0"})) : SNM_NOT_CORRECT;
    }

    @Test
    public void getWildmarkMask() {
        assert subnet1.getWildmarkMask().equals("0.0.15.255") : NOT_CORRECT;
        assert subnet2.getWildmarkMask().equals("0.0.31.255") : NOT_CORRECT;
        assert subnet3.getWildmarkMask().equals("0.0.255.255") : NOT_CORRECT;
    }

    @Test
    public void getWildmarkMask_array() {
        assert Arrays.equals(subnet1.getWildmarkMaskAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"0", "0", "15", "255"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getWildmarkMaskAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"0", "0", "31", "255"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getWildmarkMaskAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"0", "0", "255", "255"})) : NOT_CORRECT;
    }

    @Test
    public void getIQ() {
        assert subnet1.getIq() == 2 : NOT_CORRECT;
        assert subnet2.getIq() == 2 : NOT_CORRECT;
        assert subnet3.getIq() == 2 : NOT_CORRECT;
    }

    @Test
    public void getMagicNumber() {
        assert subnet1.getMagicNumber() == 16 : NOT_CORRECT;
        assert subnet2.getMagicNumber() == 32 : NOT_CORRECT;
        assert subnet3.getMagicNumber() == 256 : NOT_CORRECT;
    }

    @Test
    public void getMagicNumber_min() {
        assert subnet1.getMagicNumberMin() == 48 : NOT_CORRECT;
        assert subnet2.getMagicNumberMin() == 32 : NOT_CORRECT;
        assert subnet3.getMagicNumberMin() == 0 : NOT_CORRECT;
    }

    @Test
    public void getMagicNumber_max() {
        assert subnet1.getMagicNumberMax() == 63 : NOT_CORRECT;
        assert subnet2.getMagicNumberMax() == 63 : NOT_CORRECT;
        assert subnet3.getMagicNumberMax() == 255 : NOT_CORRECT;
    }

    @Test
    public void getSubnetID() {
        assert subnet1.getSubnetID().equals("192.168.48.0") : NOT_CORRECT;
        assert subnet2.getSubnetID().equals("192.168.32.0") : NOT_CORRECT;
        assert subnet3.getSubnetID().equals("10.0.0.0") : NOT_CORRECT;
    }

    @Test
    public void getSubnetID_array() {
        assert Arrays.equals(subnet1.getSubnetIdAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "48", "0"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getSubnetIdAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "32", "0"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getSubnetIdAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"10", "0", "0", "0"})) : NOT_CORRECT;
    }

    @Test
    public void getFirstAvailableIP() {
        assert subnet1.getFirstAvailableIp().equals("192.168.48.1") : NOT_CORRECT;
        assert subnet2.getFirstAvailableIp().equals("192.168.32.1") : NOT_CORRECT;
        assert subnet3.getFirstAvailableIp().equals("10.0.0.1") : NOT_CORRECT;
    }

    @Test
    public void getFirstAvailableIP_array() {
        assert Arrays.equals(subnet1.getFirstAvailableIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "48", "1"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getFirstAvailableIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "32", "1"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getFirstAvailableIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"10", "0", "0", "1"})) : NOT_CORRECT;
    }

    @Test
    public void getLastAvailableIP() {
        assert subnet1.getLastAvailableIp().equals("192.168.63.254") : NOT_CORRECT;
        assert subnet2.getLastAvailableIp().equals("192.168.63.254") : NOT_CORRECT;
        assert subnet3.getLastAvailableIp().equals("10.0.255.254") : NOT_CORRECT;
    }

    @Test
    public void getLastAvailableIP_array() {
        assert Arrays.equals(subnet1.getLastAvailableIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "63", "254"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getLastAvailableIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "63", "254"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getLastAvailableIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"10", "0", "255", "254"})) : NOT_CORRECT;
    }

    @Test
    public void getBroadCastIP() {
        assert subnet1.getBroadCastIp().equals("192.168.63.255") : NOT_CORRECT;
        assert subnet2.getBroadCastIp().equals("192.168.63.255") : NOT_CORRECT;
        assert subnet3.getBroadCastIp().equals("10.0.255.255") : NOT_CORRECT;
    }

    @Test
    public void getBroadCastIP_array() {
        assert Arrays.equals(subnet1.getBroadCastIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "63", "255"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getBroadCastIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "63", "255"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getBroadCastIpAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"10", "0", "255", "255"})) : NOT_CORRECT;
    }

    @Test
    public void getClassID() {
        assert subnet1.getClassID().equals("192.168.50.0") : NOT_CORRECT;
        assert subnet2.getClassID().equals("192.168.50.0") : NOT_CORRECT;
        assert subnet3.getClassID().equals("10.0.0.0") : NOT_CORRECT;
    }

    @Test
    public void getClassID_array() {
        assert Arrays.equals(subnet1.getClassIdAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "50", "0"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getClassIdAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"192", "168", "50", "0"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getClassIdAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"10", "0", "0", "0"})) : NOT_CORRECT;
    }

    @Test
    public void getClassSNM() {
        assert subnet1.getClassSnm().equals("255.255.255.0") : NOT_CORRECT;
        assert subnet2.getClassSnm().equals("255.255.255.0") : NOT_CORRECT;
        assert subnet3.getClassSnm().equals("255.0.0.0") : NOT_CORRECT;
    }

    @Test
    public void getClassSNM_array() {
        assert Arrays.equals(subnet1.getClassSnmAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "255", "0"})) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getClassSnmAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "255", "255", "0"})) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getClassSnmAsArray(), Subnet.convertStringArrayToIntegerArray(new String[]{"255", "0", "0", "0"})) : NOT_CORRECT;
    }

    @Test
    public void getClassChar() {
        assert subnet1.getClassChar() == 'C' : NOT_CORRECT;
        assert subnet2.getClassChar() == 'C' : NOT_CORRECT;
        assert subnet3.getClassChar() == 'A' : NOT_CORRECT;
    }

    @Test
    public void getNetbits() {
        assert subnet1.getNetbits() == 20 : NOT_CORRECT;
        assert subnet2.getNetbits() == 19 : NOT_CORRECT;
        assert subnet3.getNetbits() == 8 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getNetbitsString() {
        assert subnet1.getNetbitsString().equals("20 (24)") : NOT_CORRECT;
        assert subnet2.getNetbitsString().equals("19 (24)") : NOT_CORRECT;
        assert subnet3.getNetbitsString().equals("8") : NOT_CORRECT;
    }

    @Test
    public void getSubnetbits() {
        assert subnet1.getSubnetbits() == 0 : NOT_CORRECT;
        assert subnet2.getSubnetbits() == 0 : NOT_CORRECT;
        assert subnet3.getSubnetbits() == 8 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getSubnetbitsString() {
        assert subnet1.getSubnetbitsString().equals("0 (-4)") : NOT_CORRECT;
        assert subnet2.getSubnetbitsString().equals("0 (-5)") : NOT_CORRECT;
        assert subnet3.getSubnetbitsString().equals("8") : NOT_CORRECT;
    }

    @Test
    public void getHostbits() {
        assert subnet1.getHostbits() == 12 : NOT_CORRECT;
        assert subnet2.getHostbits() == 13 : NOT_CORRECT;
        assert subnet3.getHostbits() == 16 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getHostbitsString() {
        assert subnet1.getHostbitsString().equals("12") : NOT_CORRECT;
        assert subnet2.getHostbitsString().equals("13") : NOT_CORRECT;
        assert subnet3.getHostbitsString().equals("16") : NOT_CORRECT;
    }

    @Test
    public void getCountOfSubnets() {
        assert subnet1.getCountOfSubnets() == 1 : NOT_CORRECT;
        assert subnet2.getCountOfSubnets() == 1 : NOT_CORRECT;
        assert subnet3.getCountOfSubnets() == 256 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getCountOfSubnets_calc() {
        assert subnet1.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
        assert subnet2.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
        assert subnet3.getCountOfSubnetsCalc().equals("2^8 = 256") : NOT_CORRECT;
    }

    @Test
    public void getCountOfHosts() {
        assert subnet1.getCountOfHosts() == 4094 : NOT_CORRECT;
        assert subnet2.getCountOfHosts() == 8190 : NOT_CORRECT;
        assert subnet3.getCountOfHosts() == 65534 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getCountOfHosts_calc() {
        assert subnet1.getCountOfHostsCalc().equals("2^12-2 = 4094") : NOT_CORRECT;
        assert subnet2.getCountOfHostsCalc().equals("2^13-2 = 8190") : NOT_CORRECT;
        assert subnet3.getCountOfHostsCalc().equals("2^16-2 = 65534") : NOT_CORRECT;
    }

    @Test
    public void isSupernetting() {
        assert subnet1.isSupernetting() : NOT_CORRECT;
        assert subnet2.isSupernetting() : NOT_CORRECT;
        assert !subnet3.isSupernetting() : NOT_CORRECT;
    }
    // endregion

    //region extras: summarize, subnets, contains
    @Test
    public void summarize() {
        assert subnet1.summarize(subnet2).equals(new Subnet("192.168.50.0", "255.255.255.224")) : NOT_CORRECT;
        // @Test(expected = IllegalArgumentException.class)
        // assert subnet1.summarize(subnet3).equals(new Subnet("192.168.50.0", "255.255.255.224")) : NOT_CORRECT;
        // assert subnet2.summarize(subnet3).equals(new Subnet("192.168.50.0", "255.255.255.224")) : NOT_CORRECT;
    }

    @Test
    public void getSubnets() {
        // ((\d+\.){3}\d+) ((\d+\.){3}\d+)
        // new Subnet\("$1", "$3"\)
        final Set<Subnet> s1 = new TreeSet<>(Arrays.asList(new Subnet("192.168.0.0", "255.255.240.0"), new Subnet("192.168.16.0", "255.255.240.0"), new Subnet("192.168.32.0", "255.255.240.0"), new Subnet("192.168.48.0", "255.255.240.0"), new Subnet("192.168.64.0", "255.255.240.0"), new Subnet("192.168.80.0", "255.255.240.0"), new Subnet("192.168.96.0", "255.255.240.0"), new Subnet("192.168.112.0", "255.255.240.0"), new Subnet("192.168.128.0", "255.255.240.0"), new Subnet("192.168.144.0", "255.255.240.0"), new Subnet("192.168.160.0", "255.255.240.0"), new Subnet("192.168.176.0", "255.255.240.0"), new Subnet("192.168.192.0", "255.255.240.0"), new Subnet("192.168.208.0", "255.255.240.0"), new Subnet("192.168.224.0", "255.255.240.0"), new Subnet("192.168.240.0", "255.255.240.0")));
        final Set<Subnet> s2 = new TreeSet<>(Arrays.asList(new Subnet("192.168.0.0", "255.255.224.0"), new Subnet("192.168.32.0", "255.255.224.0"), new Subnet("192.168.64.0", "255.255.224.0"), new Subnet("192.168.96.0", "255.255.224.0"), new Subnet("192.168.128.0", "255.255.224.0"), new Subnet("192.168.160.0", "255.255.224.0"), new Subnet("192.168.192.0", "255.255.224.0"), new Subnet("192.168.224.0", "255.255.224.0")));
        final Set<Subnet> s3 = Collections.singleton(new Subnet("10.0.0.0", "255.255.0.0"));
        assert subnet1.getSubnets().equals(s1) : NOT_CORRECT;
        assert subnet2.getSubnets().equals(s2) : NOT_CORRECT;
        assert subnet3.getSubnets().equals(s3) : NOT_CORRECT;
    }

    @Test
    public void getSubSubnets() {
        // ((\d+\.){3}\d+) ((\d+\.){3}\d+)
        // new Subnet\("$1", "$3"\)
        // WARNING: Java Code too large for current objects
    }

    @Test
    public void isSameSubnet() {
        assert !subnet1.isSameSubnet(subnet2) : NOT_CORRECT;
        assert !subnet2.isSameSubnet(subnet1) : NOT_CORRECT;
        assert !subnet1.isSameSubnet(subnet3) : NOT_CORRECT;
        assert !subnet3.isSameSubnet(subnet1) : NOT_CORRECT;

        subnet1.setSnm("/24");
        subnet2.setSnm("/24");
        assert subnet1.isSameSubnet(subnet2) : NOT_CORRECT;
    }

    @Test
    public void contains() {
        assert !subnet1.contains(subnet2) : NOT_CORRECT;
        assert subnet2.contains(subnet1) : NOT_CORRECT;
        assert !subnet3.contains(new Subnet("11", "/24")) : NOT_CORRECT;

        subnet1.setSnm(subnet2.getSnm());
        assert subnet1.contains(subnet2) : NOT_CORRECT;
        assert subnet2.contains(subnet1) : NOT_CORRECT;
    }
    //endregion

    //region convert
    @Test
    public void convertBinaryToDecimal() {
        assert Subnet.convertBinaryToDecimal(1) == 0b1 : NOT_CORRECT;
        assert Subnet.convertBinaryToDecimal(11) == 0b11 : NOT_CORRECT;
        assert Subnet.convertBinaryToDecimal(111) == 0b111 : NOT_CORRECT;
    }

    @Test
    public void convertDecimalToBinary() {
        assert Subnet.convertDecimalToBinary(0b1) == 1 : NOT_CORRECT;
        assert Subnet.convertDecimalToBinary(0b11) == 11 : NOT_CORRECT;
        assert Subnet.convertDecimalToBinary(0b111) == 111 : NOT_CORRECT;
    }

    @Test
    public void convertIntegerArrayToStringArray() {
        assert Arrays.equals(Subnet.convertIntegerArrayToStringArray(new int[]{0, 1, 2}), new String[]{"0", "1", "2"}) : NOT_CORRECT;
    }

    @Test
    public void convertStringArrayToIntegerArray() {
        assert Arrays.equals(Subnet.convertStringArrayToIntegerArray(new String[]{"0", "1", "2"}), new int[]{0, 1, 2}) : NOT_CORRECT;
    }
    // endregion

    //region toString, compareTo, ...
    @Test
    public void toStringTest() {
        assert subnet1.toString().equals("192.168.50.20 255.255.240.0") : NOT_CORRECT;
        assert subnet2.toString().equals("192.168.50.0 255.255.224.0") : NOT_CORRECT;
        assert subnet3.toString().equals("10.0.0.0 255.255.0.0") : NOT_CORRECT;
    }

    @Test
    public void toStringDetailed() {
        final String s1 = "\nSubnet-INFO:\n192.168.50.20\t255.255.240.0\t(0.0.15.255)\tQuad: 2\t\tsupernetting\nmz:16\t\tmz:min:48\tmz:max:63\nsubnet ID: \t192.168.48.0\nBroadcast: \t192.168.63.255\nfirst available IP: \t192.168.48.1\nlast available IP: \t192.168.63.254\nclass: \tC\nclass ID: \t192.168.50.0\nclass SNM: \t255.255.255.0\nnetbits: 20 (24) \t\tsubnetbits: 0 (-4) \thostbits: 12\ncount of subnets: 2^0 = 1 \tcount of hosts: 2^12-2 = 4094";
        final String s2 = "\nSubnet-INFO:\n192.168.50.0\t255.255.224.0\t(0.0.31.255)\tQuad: 2\t\tsupernetting\nmz:32\t\tmz:min:32\tmz:max:63\nsubnet ID: \t192.168.32.0\nBroadcast: \t192.168.63.255\nfirst available IP: \t192.168.32.1\nlast available IP: \t192.168.63.254\nclass: \tC\nclass ID: \t192.168.50.0\nclass SNM: \t255.255.255.0\nnetbits: 19 (24) \t\tsubnetbits: 0 (-5) \thostbits: 13\ncount of subnets: 2^0 = 1 \tcount of hosts: 2^13-2 = 8190";
        final String s3 = "\nSubnet-INFO:\n10.0.0.0\t255.255.0.0\t(0.0.255.255)\tQuad: 2\nmz:256\t\tmz:min:0\tmz:max:255\nsubnet ID: \t10.0.0.0\nBroadcast: \t10.0.255.255\nfirst available IP: \t10.0.0.1\nlast available IP: \t10.0.255.254\nclass: \tA\nclass ID: \t10.0.0.0\nclass SNM: \t255.0.0.0\nnetbits: 8 \t\tsubnetbits: 8 \thostbits: 16\ncount of subnets: 2^8 = 256 \tcount of hosts: 2^16-2 = 65534";
        assert subnet1.toString(true).equals(s1) : NOT_CORRECT;
        assert subnet2.toString(true).equals(s2) : NOT_CORRECT;
        assert subnet3.toString(true).equals(s3) : NOT_CORRECT;
    }

    @Test
    public void copy() {
        assert subnet1.equals(subnet1.copy()) : NOT_CORRECT;
        assert subnet2.equals(subnet2.copy()) : NOT_CORRECT;
        assert subnet3.equals(subnet3.copy()) : NOT_CORRECT;
    }

    @Test
    public void compareTo() {
        assert subnet1.compareTo(subnet2) > 0 : NOT_CORRECT;
        assert subnet1.compareTo(subnet3) > 0 : NOT_CORRECT;
        assert subnet2.compareTo(subnet3) > 0 : NOT_CORRECT;
    }

    @Test
    public void equals() {
        assert subnet1.equals(new Subnet(subnet1.getIp(), subnet1.getSnm())) : NOT_CORRECT;
        assert !subnet1.equals(subnet2) : NOT_CORRECT;
        assert !subnet1.equals(subnet3) : NOT_CORRECT;
        assert !subnet2.equals(subnet3) : NOT_CORRECT;
    }

    @Test
    public void equalsDeep() {
        subnet1.setSnm("/25");
        subnet2.setSnm("/25");
        subnet3.setIp("192.168.50.128");
        subnet3.setSnm("/25");
        assert subnet1.equals(subnet2, true) : NOT_CORRECT;
        assert !subnet1.equals(subnet3, true) : NOT_CORRECT;
        assert !subnet2.equals(subnet3, true) : NOT_CORRECT;
    }

    @Test
    public void hashCodeTest() {
        assert subnet1.hashCode() == 1104073200 : NOT_CORRECT;
        assert subnet2.hashCode() == 753851720 : NOT_CORRECT;
        assert subnet3.hashCode() == 337901538 : NOT_CORRECT;
    }

    @Test
    public void iterator() {
        final Set<Subnet> subnet1subnets = new TreeSet<>();
        final Set<Subnet> subnet2subnets = new TreeSet<>();
        final Set<Subnet> subnet3subnets = new TreeSet<>();
        for (Subnet subnet : subnet1) subnet1subnets.add(subnet);
        for (Subnet subnet : subnet2) subnet2subnets.add(subnet);
        for (Subnet subnet : subnet3) subnet3subnets.add(subnet);
        assert subnet1.getSubnets().equals(subnet1subnets) : NOT_CORRECT;
        assert subnet2.getSubnets().equals(subnet2subnets) : NOT_CORRECT;
        assert subnet3.getSubnets().equals(subnet3subnets) : NOT_CORRECT;
    }
    //endregion
}