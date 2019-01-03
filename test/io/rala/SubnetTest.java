package io.rala;

import org.junit.Before;
import org.junit.Test;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class SubnetTest {
    //region default config
    private static final String NOT_CORRECT = "not correct";
    private static final String IP_NOT_CORRECT = "IP is " + NOT_CORRECT;
    private static final String SNM_NOT_CORRECT = "SNM is " + NOT_CORRECT;
    private static boolean printAll = true;
    private static boolean printAllDetailed = false;
    //endregion

    //region subnets
    private Subnet subnet1;
    private Subnet subnet2;
    private Subnet subnet3;
    private Subnet subnet4;
    private Subnet subnet5;
    //endregion

    @Before
    public void setUp() {
        subnet1 = new Subnet("10", "255"); // Class A
        subnet2 = new Subnet("128.245.97", "255.255"); // Class B
        subnet3 = new Subnet("192.168.50", "255.255.11100000"); // Class C
        subnet4 = new Subnet("224.62.83", "255.255.240"); // Class D
        subnet5 = new Subnet("240.136.42", "255.255.255"); // Class E
        if (printAll) {
            printAll = false;
            System.out.println(subnet1.toString(printAllDetailed) + (printAllDetailed ? "\n" : ""));
            System.out.println(subnet2.toString(printAllDetailed) + (printAllDetailed ? "\n" : ""));
            System.out.println(subnet3.toString(printAllDetailed) + (printAllDetailed ? "\n" : ""));
            System.out.println(subnet4.toString(printAllDetailed) + (printAllDetailed ? "\n" : ""));
            System.out.println(subnet5.toString(printAllDetailed) + (printAllDetailed ? "\n" : ""));
        }
    }

    //region constructors
    @Test
    public void constructors() {
        assert new Subnet(subnet1.getIp(), subnet1.getSubnetmask()).equals(subnet1) : NOT_CORRECT;
        assert new Subnet(subnet2.getIp().split("\\."), subnet2.getSubnetmask().split("\\.")).equals(subnet2) : NOT_CORRECT;
        assert new Subnet(subnet3.getIpAsArray(), subnet3.getSubnetmaskAsArray()).equals(subnet3) : NOT_CORRECT;

        assert new Subnet(subnet1.getIp()).equals(subnet1) : NOT_CORRECT;
        assert new Subnet(subnet2.getIp().split("\\.")).equals(subnet2) : NOT_CORRECT;
        assert new Subnet(subnet5.getIpAsArray()).equals(subnet5) : NOT_CORRECT;
    }

    @Test
    public void constructorsInterfaceAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        ArrayList<NetworkInterface> networkInterfaces = Collections.list(networkInterfaceEnumeration);
        InterfaceAddress loopback = null;

        networkInterfaces:
        for (NetworkInterface networkInterface : networkInterfaces)
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                if (interfaceAddress.getAddress().getHostAddress().equals("127.0.0.1")) {
                    loopback = interfaceAddress;
                    break networkInterfaces;
                }

        assert loopback != null : "NO LOOPBACK";
        assert new Subnet(loopback).equals(new Subnet("127.0.0.1", "/8")) : NOT_CORRECT;
    }
    //endregion

    //region setter
    @Test
    public void setIp() {
        subnet2.setIp("10");
        subnet3.setIp(new String[]{"10"});
        subnet4.setIp(new int[]{10, 0, 0, 0});
        assert subnet2.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
        assert subnet3.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
        assert subnet4.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIpEmpty() {
        subnet1.setIp("");
    }

    @Test
    public void setSubnetmask() {
        subnet1.setSubnetmask("255");
        subnet2.setSubnetmask(new String[]{"255"});
        subnet3.setSubnetmask(new int[]{255});
        assert subnet1.getSubnetmask().equals("255.0.0.0") : SNM_NOT_CORRECT;
        assert subnet2.getSubnetmask().equals("255.0.0.0") : SNM_NOT_CORRECT;
        assert subnet3.getSubnetmask().equals("255.0.0.0") : SNM_NOT_CORRECT;
    }

    @Test
    public void setSubnetmaskBasedOnClass() {
        subnet1.setSubnetmaskBasedOnClass();
        subnet2.setSubnetmaskBasedOnClass();
        subnet3.setSubnetmaskBasedOnClass();
        subnet4.setSubnetmaskBasedOnClass();
        subnet5.setSubnetmaskBasedOnClass();

        assert subnet1.getSubnetmask().equals("255.0.0.0") : SNM_NOT_CORRECT;
        assert subnet2.getSubnetmask().equals("255.255.0.0") : SNM_NOT_CORRECT;
        assert subnet3.getSubnetmask().equals("255.255.255.0") : SNM_NOT_CORRECT;
        assert subnet4.getSubnetmask().equals("255.255.255.0") : SNM_NOT_CORRECT;
        assert subnet5.getSubnetmask().equals("255.255.255.0") : SNM_NOT_CORRECT;
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSubnetmaskEmpty() {
        subnet1.setSubnetmask("");
    }
    //endregion

    //region getter (basic)
    @Test
    public void getIp() {
        assert subnet1.getIp().equals("10.0.0.0") : IP_NOT_CORRECT;
        assert subnet2.getIp().equals("128.245.97.0") : IP_NOT_CORRECT;
        assert subnet3.getIp().equals("192.168.50.0") : IP_NOT_CORRECT;
        assert subnet4.getIp().equals("224.62.83.0") : IP_NOT_CORRECT;
        assert subnet5.getIp().equals("240.136.42.0") : IP_NOT_CORRECT;
    }

    @Test
    public void getIpAsArray() {
        assert Arrays.equals(subnet1.getIpAsArray(), new int[]{10, 0, 0, 0}) : IP_NOT_CORRECT;
        assert Arrays.equals(subnet2.getIpAsArray(), new int[]{128, 245, 97, 0}) : IP_NOT_CORRECT;
        assert Arrays.equals(subnet3.getIpAsArray(), new int[]{192, 168, 50, 0}) : IP_NOT_CORRECT;
        assert Arrays.equals(subnet4.getIpAsArray(), new int[]{224, 62, 83, 0}) : IP_NOT_CORRECT;
        assert Arrays.equals(subnet5.getIpAsArray(), new int[]{240, 136, 42, 0}) : IP_NOT_CORRECT;
    }

    @Test
    public void getSubnetmask() {
        assert subnet1.getSubnetmask().equals("255.0.0.0") : SNM_NOT_CORRECT;
        assert subnet2.getSubnetmask().equals("255.255.0.0") : SNM_NOT_CORRECT;
        assert subnet3.getSubnetmask().equals("255.255.224.0") : SNM_NOT_CORRECT;
        assert subnet4.getSubnetmask().equals("255.255.240.0") : SNM_NOT_CORRECT;
        assert subnet5.getSubnetmask().equals("255.255.255.0") : SNM_NOT_CORRECT;
    }

    @Test
    public void getSubnetmaskAsArray() {
        assert Arrays.equals(subnet1.getSubnetmaskAsArray(), new int[]{255, 0, 0, 0}) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet2.getSubnetmaskAsArray(), new int[]{255, 255, 0, 0}) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet3.getSubnetmaskAsArray(), new int[]{255, 255, 224, 0}) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet4.getSubnetmaskAsArray(), new int[]{255, 255, 240, 0}) : SNM_NOT_CORRECT;
        assert Arrays.equals(subnet5.getSubnetmaskAsArray(), new int[]{255, 255, 255, 0}) : SNM_NOT_CORRECT;
    }

    @Test
    public void getWildmarkMask() {
        assert subnet1.getWildmarkMask().equals("0.255.255.255") : NOT_CORRECT;
        assert subnet2.getWildmarkMask().equals("0.0.255.255") : NOT_CORRECT;
        assert subnet3.getWildmarkMask().equals("0.0.31.255") : NOT_CORRECT;
        assert subnet4.getWildmarkMask().equals("0.0.15.255") : NOT_CORRECT;
        assert subnet5.getWildmarkMask().equals("0.0.0.255") : NOT_CORRECT;
    }

    @Test
    public void getWildmarkMaskAsArray() {
        assert Arrays.equals(subnet1.getWildmarkMaskAsArray(), new int[]{0, 255, 255, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getWildmarkMaskAsArray(), new int[]{0, 0, 255, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getWildmarkMaskAsArray(), new int[]{0, 0, 31, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getWildmarkMaskAsArray(), new int[]{0, 0, 15, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getWildmarkMaskAsArray(), new int[]{0, 0, 0, 255}) : NOT_CORRECT;
    }

    @Test
    public void getIq() {
        assert subnet1.getIq() == 1 : NOT_CORRECT;
        assert subnet2.getIq() == 2 : NOT_CORRECT;
        assert subnet3.getIq() == 2 : NOT_CORRECT;
        assert subnet4.getIq() == 2 : NOT_CORRECT;
        assert subnet5.getIq() == 3 : NOT_CORRECT;
    }

    @Test
    public void getMagicNumber() {
        assert subnet1.getMagicNumber() == 256 : NOT_CORRECT;
        assert subnet2.getMagicNumber() == 256 : NOT_CORRECT;
        assert subnet3.getMagicNumber() == 32 : NOT_CORRECT;
        assert subnet4.getMagicNumber() == 16 : NOT_CORRECT;
        assert subnet5.getMagicNumber() == 256 : NOT_CORRECT;
    }

    @Test
    public void getMagicNumberMin() {
        assert subnet1.getMagicNumberMin() == 0 : NOT_CORRECT;
        assert subnet2.getMagicNumberMin() == 0 : NOT_CORRECT;
        assert subnet3.getMagicNumberMin() == 32 : NOT_CORRECT;
        assert subnet4.getMagicNumberMin() == 80 : NOT_CORRECT;
        assert subnet5.getMagicNumberMin() == 0 : NOT_CORRECT;
    }

    @Test
    public void getMagicNumberMax() {
        assert subnet1.getMagicNumberMax() == 255 : NOT_CORRECT;
        assert subnet2.getMagicNumberMax() == 255 : NOT_CORRECT;
        assert subnet3.getMagicNumberMax() == 63 : NOT_CORRECT;
        assert subnet4.getMagicNumberMax() == 95 : NOT_CORRECT;
        assert subnet5.getMagicNumberMax() == 255 : NOT_CORRECT;
    }

    @Test
    public void getSubnetId() {
        assert subnet1.getSubnetId().equals("10.0.0.0") : NOT_CORRECT;
        assert subnet2.getSubnetId().equals("128.245.0.0") : NOT_CORRECT;
        assert subnet3.getSubnetId().equals("192.168.32.0") : NOT_CORRECT;
        assert subnet4.getSubnetId().equals("224.62.80.0") : NOT_CORRECT;
        assert subnet5.getSubnetId().equals("240.136.42.0") : NOT_CORRECT;
    }

    @Test
    public void getSubnetIdAsArray() {
        assert Arrays.equals(subnet1.getSubnetIdAsArray(), new int[]{10, 0, 0, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getSubnetIdAsArray(), new int[]{128, 245, 0, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getSubnetIdAsArray(), new int[]{192, 168, 32, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getSubnetIdAsArray(), new int[]{224, 62, 80, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getSubnetIdAsArray(), new int[]{240, 136, 42, 0}) : NOT_CORRECT;
    }

    @Test
    public void getFirstAvailableIp() {
        assert subnet1.getFirstAvailableIp().equals("10.0.0.1") : NOT_CORRECT;
        assert subnet2.getFirstAvailableIp().equals("128.245.0.1") : NOT_CORRECT;
        assert subnet3.getFirstAvailableIp().equals("192.168.32.1") : NOT_CORRECT;
        assert subnet4.getFirstAvailableIp().equals("224.62.80.1") : NOT_CORRECT;
        assert subnet5.getFirstAvailableIp().equals("240.136.42.1") : NOT_CORRECT;
    }

    @Test
    public void getFirstAvailableIpAsArray() {
        assert Arrays.equals(subnet1.getFirstAvailableIpAsArray(), new int[]{10, 0, 0, 1}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getFirstAvailableIpAsArray(), new int[]{128, 245, 0, 1}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getFirstAvailableIpAsArray(), new int[]{192, 168, 32, 1}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getFirstAvailableIpAsArray(), new int[]{224, 62, 80, 1}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getFirstAvailableIpAsArray(), new int[]{240, 136, 42, 1}) : NOT_CORRECT;
    }

    @Test
    public void getLastAvailableIp() {
        assert subnet1.getLastAvailableIp().equals("10.255.255.254") : NOT_CORRECT;
        assert subnet2.getLastAvailableIp().equals("128.245.255.254") : NOT_CORRECT;
        assert subnet3.getLastAvailableIp().equals("192.168.63.254") : NOT_CORRECT;
        assert subnet4.getLastAvailableIp().equals("224.62.95.254") : NOT_CORRECT;
        assert subnet5.getLastAvailableIp().equals("240.136.42.254") : NOT_CORRECT;
    }

    @Test
    public void getLastAvailableIpAsArray() {
        assert Arrays.equals(subnet1.getLastAvailableIpAsArray(), new int[]{10, 255, 255, 254}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getLastAvailableIpAsArray(), new int[]{128, 245, 255, 254}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getLastAvailableIpAsArray(), new int[]{192, 168, 63, 254}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getLastAvailableIpAsArray(), new int[]{224, 62, 95, 254}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getLastAvailableIpAsArray(), new int[]{240, 136, 42, 254}) : NOT_CORRECT;
    }

    @Test
    public void getBroadCastIp() {
        assert subnet1.getBroadCastIp().equals("10.255.255.255") : NOT_CORRECT;
        assert subnet2.getBroadCastIp().equals("128.245.255.255") : NOT_CORRECT;
        assert subnet3.getBroadCastIp().equals("192.168.63.255") : NOT_CORRECT;
        assert subnet4.getBroadCastIp().equals("224.62.95.255") : NOT_CORRECT;
        assert subnet5.getBroadCastIp().equals("240.136.42.255") : NOT_CORRECT;
    }

    @Test
    public void getBroadCastIpAsArray() {
        assert Arrays.equals(subnet1.getBroadCastIpAsArray(), new int[]{10, 255, 255, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getBroadCastIpAsArray(), new int[]{128, 245, 255, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getBroadCastIpAsArray(), new int[]{192, 168, 63, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getBroadCastIpAsArray(), new int[]{224, 62, 95, 255}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getBroadCastIpAsArray(), new int[]{240, 136, 42, 255}) : NOT_CORRECT;
    }

    @Test
    public void getClassId() {
        assert subnet1.getClassId().equals("10.0.0.0") : NOT_CORRECT;
        assert subnet2.getClassId().equals("128.245.0.0") : NOT_CORRECT;
        assert subnet3.getClassId().equals("192.168.50.0") : NOT_CORRECT;
        assert subnet4.getClassId().equals("224.62.83.0") : NOT_CORRECT;
        assert subnet5.getClassId().equals("240.136.42.0") : NOT_CORRECT;
    }

    @Test
    public void getClassIdAsArray() {
        assert Arrays.equals(subnet1.getClassIdAsArray(), new int[]{10, 0, 0, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getClassIdAsArray(), new int[]{128, 245, 0, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getClassIdAsArray(), new int[]{192, 168, 50, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getClassIdAsArray(), new int[]{224, 62, 83, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getClassIdAsArray(), new int[]{240, 136, 42, 0}) : NOT_CORRECT;
    }

    @Test
    public void getClassSubnetmask() {
        assert subnet1.getClassSubnetmask().equals("255.0.0.0") : NOT_CORRECT;
        assert subnet2.getClassSubnetmask().equals("255.255.0.0") : NOT_CORRECT;
        assert subnet3.getClassSubnetmask().equals("255.255.255.0") : NOT_CORRECT;
        assert subnet4.getClassSubnetmask().equals("255.255.255.0") : NOT_CORRECT;
        assert subnet5.getClassSubnetmask().equals("255.255.255.0") : NOT_CORRECT;
    }

    @Test
    public void getClassSubnetmaskAsArray() {
        assert Arrays.equals(subnet1.getClassSubnetmaskAsArray(), new int[]{255, 0, 0, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet2.getClassSubnetmaskAsArray(), new int[]{255, 255, 0, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet3.getClassSubnetmaskAsArray(), new int[]{255, 255, 255, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet4.getClassSubnetmaskAsArray(), new int[]{255, 255, 255, 0}) : NOT_CORRECT;
        assert Arrays.equals(subnet5.getClassSubnetmaskAsArray(), new int[]{255, 255, 255, 0}) : NOT_CORRECT;
    }

    @Test
    public void getClassChar() {
        assert subnet1.getClassChar() == 'A' : NOT_CORRECT;
        assert subnet2.getClassChar() == 'B' : NOT_CORRECT;
        assert subnet3.getClassChar() == 'C' : NOT_CORRECT;
        assert subnet4.getClassChar() == 'D' : NOT_CORRECT;
        assert subnet5.getClassChar() == 'E' : NOT_CORRECT;
    }

    @Test
    public void getNetbits() {
        assert subnet1.getNetbits() == 8 : NOT_CORRECT;
        assert subnet2.getNetbits() == 16 : NOT_CORRECT;
        assert subnet3.getNetbits() == 19 : NOT_CORRECT;
        assert subnet4.getNetbits() == 20 : NOT_CORRECT;
        assert subnet5.getNetbits() == 24 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getNetbitsString() {
        assert subnet1.getNetbitsString().equals("8") : NOT_CORRECT;
        assert subnet2.getNetbitsString().equals("16") : NOT_CORRECT;
        assert subnet3.getNetbitsString().equals("19 (24)") : NOT_CORRECT;
        assert subnet4.getNetbitsString().equals("20") : NOT_CORRECT;
        assert subnet5.getNetbitsString().equals("24") : NOT_CORRECT;
    }

    @Test
    public void getSubnetbits() {
        assert subnet1.getSubnetbits() == 0 : NOT_CORRECT;
        assert subnet2.getSubnetbits() == 0 : NOT_CORRECT;
        assert subnet3.getSubnetbits() == 0 : NOT_CORRECT;
        assert subnet4.getSubnetbits() == 0 : NOT_CORRECT;
        assert subnet5.getSubnetbits() == 0 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getSubnetbitsString() {
        assert subnet1.getSubnetbitsString().equals("0") : NOT_CORRECT;
        assert subnet2.getSubnetbitsString().equals("0") : NOT_CORRECT;
        assert subnet3.getSubnetbitsString().equals("0 (-5)") : NOT_CORRECT;
        assert subnet4.getSubnetbitsString().equals("0") : NOT_CORRECT;
        assert subnet5.getSubnetbitsString().equals("0") : NOT_CORRECT;
    }

    @Test
    public void getHostbits() {
        assert subnet1.getHostbits() == 24 : NOT_CORRECT;
        assert subnet2.getHostbits() == 16 : NOT_CORRECT;
        assert subnet3.getHostbits() == 13 : NOT_CORRECT;
        assert subnet4.getHostbits() == 12 : NOT_CORRECT;
        assert subnet5.getHostbits() == 8 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getHostbitsString() {
        assert subnet1.getHostbitsString().equals("24") : NOT_CORRECT;
        assert subnet2.getHostbitsString().equals("16") : NOT_CORRECT;
        assert subnet3.getHostbitsString().equals("13") : NOT_CORRECT;
        assert subnet4.getHostbitsString().equals("12") : NOT_CORRECT;
        assert subnet5.getHostbitsString().equals("8") : NOT_CORRECT;
    }

    @Test
    public void getCountOfSubnets() {
        assert subnet1.getCountOfSubnets() == 1 : NOT_CORRECT;
        assert subnet2.getCountOfSubnets() == 1 : NOT_CORRECT;
        assert subnet3.getCountOfSubnets() == 1 : NOT_CORRECT;
        assert subnet4.getCountOfSubnets() == 1 : NOT_CORRECT;
        assert subnet5.getCountOfSubnets() == 1 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getCountOfSubnetsCalc() {
        assert subnet1.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
        assert subnet2.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
        assert subnet3.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
        assert subnet4.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
        assert subnet5.getCountOfSubnetsCalc().equals("2^0 = 1") : NOT_CORRECT;
    }

    @Test
    public void getCountOfHosts() {
        assert subnet1.getCountOfHosts() == 16777214 : NOT_CORRECT;
        assert subnet2.getCountOfHosts() == 65534 : NOT_CORRECT;
        assert subnet3.getCountOfHosts() == 8190 : NOT_CORRECT;
        assert subnet4.getCountOfHosts() == 4094 : NOT_CORRECT;
        assert subnet5.getCountOfHosts() == 254 : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("AssertWithSideEffects")
    public void getCountOfHostsCalc() {
        assert subnet1.getCountOfHostsCalc().equals("2^24-2 = 16777214") : NOT_CORRECT;
        assert subnet2.getCountOfHostsCalc().equals("2^16-2 = 65534") : NOT_CORRECT;
        assert subnet3.getCountOfHostsCalc().equals("2^13-2 = 8190") : NOT_CORRECT;
        assert subnet4.getCountOfHostsCalc().equals("2^12-2 = 4094") : NOT_CORRECT;
        assert subnet5.getCountOfHostsCalc().equals("2^8-2 = 254") : NOT_CORRECT;
    }

    @Test
    public void isSupernetting() {
        assert !subnet1.isSupernetting() : NOT_CORRECT;
        assert !subnet2.isSupernetting() : NOT_CORRECT;
        assert subnet3.isSupernetting() : NOT_CORRECT;
        assert !subnet4.isSupernetting() : NOT_CORRECT;
        assert !subnet5.isSupernetting() : NOT_CORRECT;
    }
    // endregion

    //region extras: summarize, subnets, contains
    @Test
    public void summarize() {
        subnet2.setIp("192.168.20");
        assert subnet2.summarize(subnet3).equals(new Subnet("192.168.0.0", "255.255.192.0")) : NOT_CORRECT;
        assert subnet3.summarize(subnet3).equals(new Subnet("192.168.50.0", "255.255.255.252")) : NOT_CORRECT;
    }

    @Test
    public void summarizeDifferent() {
        subnet2.setIp("192.167.20");
        assert subnet2.summarize(subnet3).equals(new Subnet("192.160.0.0", "255.240.0.0")) : NOT_CORRECT;
    }

    @Test(expected = IllegalArgumentException.class)
    public void summarizeWithException() {
        assert subnet1.summarize(subnet3) == null : NOT_CORRECT;
        assert subnet2.summarize(subnet3) == null : NOT_CORRECT;
        // all other combinations throw also an exception
    }

    @Test
    public void summarizeMultiple() {
        subnet1.setIp("192.168.0");
        subnet2.setIp("192.168.20");
        assert subnet1.summarize(subnet2, subnet3).equals(new Subnet("192.168.0.0", "255.255.192.0")) : NOT_CORRECT;
    }

    @Test
    public void summarizeMultipleDifferent() {
        subnet1.setIp("192.167.5");
        subnet2.setIp("192.169.20");
        assert subnet1.summarize(Arrays.asList(subnet2, subnet3)).equals(new Subnet("192.160.0.0", "255.240.0.0")) : NOT_CORRECT;
    }

    @Test
    public void getSubnets() {
        final Set<Subnet> s1 = Collections.singleton(new Subnet("10.0.0.0", "255.0.0.0"));
        final Set<Subnet> s2 = Collections.singleton(new Subnet("128.245.0.0", "255.255.0.0"));
        final Set<Subnet> s3 = new TreeSet<>(Arrays.asList(
            new Subnet("192.168.0.0", "255.255.224.0"),
            new Subnet("192.168.32.0", "255.255.224.0"),
            new Subnet("192.168.64.0", "255.255.224.0"),
            new Subnet("192.168.96.0", "255.255.224.0"),
            new Subnet("192.168.128.0", "255.255.224.0"),
            new Subnet("192.168.160.0", "255.255.224.0"),
            new Subnet("192.168.192.0", "255.255.224.0"),
            new Subnet("192.168.224.0", "255.255.224.0")));
        final Set<Subnet> s4 = new TreeSet<>(Arrays.asList(
            new Subnet("224.62.0.0", "255.255.240.0"),
            new Subnet("224.62.16.0", "255.255.240.0"),
            new Subnet("224.62.32.0", "255.255.240.0"),
            new Subnet("224.62.48.0", "255.255.240.0"),
            new Subnet("224.62.64.0", "255.255.240.0"),
            new Subnet("224.62.80.0", "255.255.240.0"),
            new Subnet("224.62.96.0", "255.255.240.0"),
            new Subnet("224.62.112.0", "255.255.240.0"),
            new Subnet("224.62.128.0", "255.255.240.0"),
            new Subnet("224.62.144.0", "255.255.240.0"),
            new Subnet("224.62.160.0", "255.255.240.0"),
            new Subnet("224.62.176.0", "255.255.240.0"),
            new Subnet("224.62.192.0", "255.255.240.0"),
            new Subnet("224.62.208.0", "255.255.240.0"),
            new Subnet("224.62.224.0", "255.255.240.0"),
            new Subnet("224.62.240.0", "255.255.240.0")));
        final Set<Subnet> s5 = Collections.singleton(new Subnet("240.136.42.0", "255.255.255.0"));
        assert subnet1.getSubnets().equals(s1) : NOT_CORRECT;
        assert subnet2.getSubnets().equals(s2) : NOT_CORRECT;
        assert subnet3.getSubnets().equals(s3) : NOT_CORRECT;
        assert subnet4.getSubnets().equals(s4) : NOT_CORRECT;
        assert subnet5.getSubnets().equals(s5) : NOT_CORRECT;
    }

    @Test
    public void getSubSubnets() {
        // System.out.println(subnet1.getSubSubnets()); // takes to long
        // System.out.println(subnet2.getSubSubnets()); // takes to long
        // System.out.println(subnet3.getSubSubnets()); // takes to long
        // System.out.println(subnet4.getSubSubnets()); // takes to long
        // System.out.println(subnet5.getSubSubnets()); // acceptable - but still a long list
        subnet5.setSubnetmask("/28");
        Set<Subnet> s5 = new TreeSet<>(Arrays.asList(new Subnet("240.136.42.1", "255.255.255.240"),
            new Subnet("240.136.42.2", "255.255.255.240"),
            new Subnet("240.136.42.3", "255.255.255.240"),
            new Subnet("240.136.42.4", "255.255.255.240"),
            new Subnet("240.136.42.5", "255.255.255.240"),
            new Subnet("240.136.42.6", "255.255.255.240"),
            new Subnet("240.136.42.7", "255.255.255.240"),
            new Subnet("240.136.42.8", "255.255.255.240"),
            new Subnet("240.136.42.9", "255.255.255.240"),
            new Subnet("240.136.42.10", "255.255.255.240"),
            new Subnet("240.136.42.11", "255.255.255.240"),
            new Subnet("240.136.42.12", "255.255.255.240"),
            new Subnet("240.136.42.13", "255.255.255.240"),
            new Subnet("240.136.42.14", "255.255.255.240")));
        assert subnet5.getSubSubnets().equals(s5) : NOT_CORRECT;
    }

    @Test
    public void isSameSubnet() {
        assert !subnet1.isSameSubnet(subnet2) : NOT_CORRECT;
        assert !subnet2.isSameSubnet(subnet1) : NOT_CORRECT;
        assert !subnet1.isSameSubnet(subnet3) : NOT_CORRECT;
        assert !subnet3.isSameSubnet(subnet1) : NOT_CORRECT;
        // all other combinations are also not the same subnet

        subnet2.setIp("192.168.20");
        subnet3.setSubnetmask("255.255");
        assert subnet2.isSameSubnet(subnet3) : NOT_CORRECT;
    }

    @Test
    public void contains() {
        assert !subnet1.contains(subnet2) : NOT_CORRECT;
        assert !subnet2.contains(subnet1) : NOT_CORRECT;
        assert !subnet1.contains(subnet3) : NOT_CORRECT;
        assert !subnet3.contains(subnet1) : NOT_CORRECT;
        // all other combinations do also not contain each other

        subnet2.setIp("192.168.20");
        assert subnet2.contains(subnet3) : NOT_CORRECT;
        assert !subnet3.contains(subnet2) : NOT_CORRECT;
    }
    //endregion

    //region validate
    @Test(expected = IllegalArgumentException.class)
    public void checkEntryAndConvertSubnetmaskWithMissingIp() {
        new Subnet("", "255");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEntryAndConvertSubnetmaskWithMissingSubnetmask() {
        new Subnet("10", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEntryAndConvertSubnetmaskWithInvalidIp() {
        new Subnet("/10", "255");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEntryAndConvertSubnetmaskWithToLargeIp() {
        new Subnet("256", "255");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEntryAndConvertSubnetmaskWithInvalidSubnetmask() {
        new Subnet("10", "255,");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEntryAndConvertSubnetmaskWithToLargeSubnetmask() {
        new Subnet("10", "255.111111110");
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertPrefixAndValidateWithMissingNumber() {
        new Subnet("10", "/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertPrefixAndValidateWithInvalidNumber() {
        new Subnet("10", "/24,");
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertPrefixAndValidateWithToLowNumber() {
        new Subnet("10", "/1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertPrefixAndValidateWithToHighNumber() {
        new Subnet("10", "/32");
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertBinarySubnetmaskToDecimalWithToLargeNumberInLastQuad() {
        new Subnet("10", "255.255.255.255");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSubnetOkWithWrongSubnetmaskNumber() {
        new Subnet("10", "10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSubnetOkWithInvalidSubnetmaskPatternUnequal0() {
        new Subnet("10", "255.240.240");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSubnetOkWithInvalidSubnetmaskPatternAfter0() {
        new Subnet("10", "255.0.255");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMagicNumber() {
        subnet1.setSubnetmask("128");
    }
    //endregion

    //region convert
    @Test
    public void convertBinaryToDecimal() {
        assert Subnet.convertBinaryToDecimal("1") == 0b1 : NOT_CORRECT;
        assert Subnet.convertBinaryToDecimal("11") == 0b11 : NOT_CORRECT;
        assert Subnet.convertBinaryToDecimal("111") == 0b111 : NOT_CORRECT;

        assert Subnet.convertBinaryToDecimal(1) == 0b1 : NOT_CORRECT;
        assert Subnet.convertBinaryToDecimal(11) == 0b11 : NOT_CORRECT;
        assert Subnet.convertBinaryToDecimal(111) == 0b111 : NOT_CORRECT;
    }

    @Test
    public void convertDecimalToBinary() {
        assert Subnet.convertDecimalToBinary("1") == 1 : NOT_CORRECT;
        assert Subnet.convertDecimalToBinary("3") == 11 : NOT_CORRECT;
        assert Subnet.convertDecimalToBinary("7") == 111 : NOT_CORRECT;

        assert Subnet.convertDecimalToBinary(0b1) == 1 : NOT_CORRECT;
        assert Subnet.convertDecimalToBinary(0b11) == 11 : NOT_CORRECT;
        assert Subnet.convertDecimalToBinary(0b111) == 111 : NOT_CORRECT;
    }

    @Test
    public void convertIntegerArrayToStringArray() {
        assert Arrays.equals(Subnet.convertIntegerArrayToStringArray(
            new int[]{0, 1, 2}),
            new String[]{"0", "1", "2"}) : NOT_CORRECT;
    }

    @Test
    public void convertStringArrayToIntegerArray() {
        assert Arrays.equals(Subnet.convertStringArrayToIntegerArray(
            new String[]{"0", "1", "2"}),
            new int[]{0, 1, 2}) : NOT_CORRECT;
    }
    // endregion

    //region toString, compareTo, ...
    @Test
    public void toStringTest() {
        assert subnet1.toString().equals("10.0.0.0 255.0.0.0") : NOT_CORRECT;
        assert subnet2.toString().equals("128.245.97.0 255.255.0.0") : NOT_CORRECT;
        assert subnet3.toString().equals("192.168.50.0 255.255.224.0") : NOT_CORRECT;
        assert subnet4.toString().equals("224.62.83.0 255.255.240.0") : NOT_CORRECT;
        assert subnet5.toString().equals("240.136.42.0 255.255.255.0") : NOT_CORRECT;
    }

    @Test
    public void toStringDetailed() {
        final String s1 = "Subnet-INFO:\n" +
                              "10.0.0.0        255.0.0.0       (0.255.255.255)   Quad: 1\n" +
                              "mz: 256         mz:min: 0       mz:max: 255\n" +
                              "subnet ID:      10.0.0.0\n" +
                              "broadcast:      10.255.255.255\n" +
                              "first available IP:  10.0.0.1\n" +
                              "last available IP:   10.255.255.254\n" +
                              "class:          A\n" +
                              "class ID:       10.0.0.0\n" +
                              "class SNM:      255.0.0.0\n" +
                              "netbits:        8               subnetbits:     0               hostbits:       24\n" +
                              "count of subnets:    2^0 = 1    count of hosts:      2^24-2 = 16777214";
        final String s2 = "Subnet-INFO:\n" +
                              "128.245.97.0    255.255.0.0     (0.0.255.255)     Quad: 2\n" +
                              "mz: 256         mz:min: 0       mz:max: 255\n" +
                              "subnet ID:      128.245.0.0\n" +
                              "broadcast:      128.245.255.255\n" +
                              "first available IP:  128.245.0.1\n" +
                              "last available IP:   128.245.255.254\n" +
                              "class:          B\n" +
                              "class ID:       128.245.0.0\n" +
                              "class SNM:      255.255.0.0\n" +
                              "netbits:        16              subnetbits:     0               hostbits:       16\n" +
                              "count of subnets:    2^0 = 1    count of hosts:      2^16-2 = 65534";
        final String s3 = "Subnet-INFO:\n" +
                              "192.168.50.0    255.255.224.0   (0.0.31.255)      Quad: 2       supernetting\n" +
                              "mz: 32          mz:min: 32      mz:max: 63\n" +
                              "subnet ID:      192.168.32.0\n" +
                              "broadcast:      192.168.63.255\n" +
                              "first available IP:  192.168.32.1\n" +
                              "last available IP:   192.168.63.254\n" +
                              "class:          C\n" +
                              "class ID:       192.168.50.0\n" +
                              "class SNM:      255.255.255.0\n" +
                              "netbits:        19 (24)         subnetbits:     0 (-5)          hostbits:       13\n" +
                              "count of subnets:    2^0 = 1    count of hosts:      2^13-2 = 8190";
        final String s4 = "Subnet-INFO:\n" +
                              "224.62.83.0     255.255.240.0   (0.0.15.255)      Quad: 2\n" +
                              "mz: 16          mz:min: 80      mz:max: 95\n" +
                              "subnet ID:      224.62.80.0\n" +
                              "broadcast:      224.62.95.255\n" +
                              "first available IP:  224.62.80.1\n" +
                              "last available IP:   224.62.95.254\n" +
                              "class:          D\n" +
                              "class ID:       224.62.83.0\n" +
                              "class SNM:      255.255.255.0\n" +
                              "netbits:        20              subnetbits:     0               hostbits:       12\n" +
                              "count of subnets:    2^0 = 1    count of hosts:      2^12-2 = 4094";
        final String s5 = "Subnet-INFO:\n" +
                              "240.136.42.0    255.255.255.0   (0.0.0.255)       Quad: 3\n" +
                              "mz: 256         mz:min: 0       mz:max: 255\n" +
                              "subnet ID:      240.136.42.0\n" +
                              "broadcast:      240.136.42.255\n" +
                              "first available IP:  240.136.42.1\n" +
                              "last available IP:   240.136.42.254\n" +
                              "class:          E\n" +
                              "class ID:       240.136.42.0\n" +
                              "class SNM:      255.255.255.0\n" +
                              "netbits:        24              subnetbits:     0               hostbits:       8\n" +
                              "count of subnets:    2^0 = 1    count of hosts:      2^8-2 = 254";
        assert subnet1.toString(true).equals(s1) : NOT_CORRECT;
        assert subnet2.toString(true).equals(s2) : NOT_CORRECT;
        assert subnet3.toString(true).equals(s3) : NOT_CORRECT;
        assert subnet4.toString(true).equals(s4) : NOT_CORRECT;
        assert subnet5.toString(true).equals(s5) : NOT_CORRECT;
    }

    @Test
    public void copy() {
        assert subnet1.equals(subnet1.copy()) : NOT_CORRECT;
        assert subnet2.equals(subnet2.copy()) : NOT_CORRECT;
        assert subnet3.equals(subnet3.copy()) : NOT_CORRECT;
    }

    @Test
    public void compareTo() {
        assert 0 < subnet2.compareTo(subnet1) : NOT_CORRECT;
        assert 0 < subnet3.compareTo(subnet2) : NOT_CORRECT;
        assert 0 < subnet4.compareTo(subnet3) : NOT_CORRECT;
        assert 0 < subnet5.compareTo(subnet4) : NOT_CORRECT;
    }

    @Test
    public void equals() {
        assert subnet1.equals(new Subnet(subnet1.getIp(), subnet1.getSubnetmask())) : NOT_CORRECT;
        assert !subnet1.equals(subnet2) : NOT_CORRECT;
        assert !subnet1.equals(subnet3) : NOT_CORRECT;
        assert !subnet2.equals(subnet3) : NOT_CORRECT;
    }

    @Test
    public void equalsDeep() {
        subnet2.setIp("192.168.50.128");
        subnet2.setSubnetmask(subnet3.getSubnetmask());
        assert !subnet1.equals(subnet3, true) : NOT_CORRECT;
        assert subnet2.equals(subnet3, true) : NOT_CORRECT;
    }

    @Test
    public void hashCodeTest() {
        assert subnet1.hashCode() == -838740488 : NOT_CORRECT;
        assert subnet2.hashCode() == -1722502892 : NOT_CORRECT;
        assert subnet3.hashCode() == 753851720 : NOT_CORRECT;
        assert subnet4.hashCode() == -1887865192 : NOT_CORRECT;
        assert subnet5.hashCode() == 701803096 : NOT_CORRECT;
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

    //region deprecated
    @Test
    @SuppressWarnings("deprecation")
    public void setIpDeprecation() {
        subnet2.setIp("10", false);
        assert subnet2.toString().equals("10.0.0.0 255.255.0.0") : NOT_CORRECT;
        assert subnet2.getClassChar() == 'B' : NOT_CORRECT;

        subnet2.setIp(new String[]{"11", "0", "0", "0"}, false);
        assert subnet2.toString().equals("11.0.0.0 255.255.0.0") : NOT_CORRECT;
        assert subnet2.getClassChar() == 'B' : NOT_CORRECT;

        subnet2.setIp(new int[]{10, 0, 0, 0}, false);
        assert subnet2.toString().equals("10.0.0.0 255.255.0.0") : NOT_CORRECT;
        assert subnet2.getClassChar() == 'B' : NOT_CORRECT;
    }

    @Test
    @SuppressWarnings("deprecation")
    public void recalculateDeprecation() { // see also setIpDeprecation
        subnet2.setIp("10", false);
        assert subnet2.toString().equals("10.0.0.0 255.255.0.0") : NOT_CORRECT;
        assert subnet2.getClassChar() == 'B' : NOT_CORRECT;

        subnet2.recalculate();
        assert subnet2.toString().equals("10.0.0.0 255.255.0.0") : NOT_CORRECT;
        assert subnet2.getClassChar() == 'A' : NOT_CORRECT;
    }
    //endregion
}