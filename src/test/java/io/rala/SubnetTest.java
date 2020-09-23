package io.rala;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SubnetTest {
    //region default config
    private static boolean printAll = false;
    @SuppressWarnings("FieldCanBeLocal")
    private static final boolean printAllDetailed = false;
    //endregion

    //region subnets
    private Subnet subnet1;
    private Subnet subnet2;
    private Subnet subnet3;
    private Subnet subnet4;
    private Subnet subnet5;
    //endregion

    @BeforeEach
    void beforeEach() {
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
    void constructors() {
        assertEquals(subnet1, new Subnet(subnet1.getIp(), subnet1.getSubnetmask()));
        assertEquals(subnet2, new Subnet(subnet2.getIp().split("\\."), subnet2.getSubnetmask().split("\\.")));
        assertEquals(subnet3, new Subnet(subnet3.getIpAsArray(), subnet3.getSubnetmaskAsArray()));

        assertEquals(subnet1, new Subnet(subnet1.getIp()));
        assertEquals(subnet2, new Subnet(subnet2.getIp().split("\\.")));
        assertEquals(subnet5, new Subnet(subnet5.getIpAsArray()));
    }

    @Test
    void constructorsInterfaceAddress() throws SocketException {
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

        assertNotNull(loopback);
        assertEquals(new Subnet("127.0.0.1", "/8"), new Subnet(loopback));
    }
    //endregion

    //region setter
    @Test
    void setIp() {
        subnet2.setIp("10");
        subnet3.setIp(new String[]{"10"});
        subnet4.setIp(new int[]{10, 0, 0, 0});
        assertEquals("10.0.0.0", subnet2.getIp());
        assertEquals("10.0.0.0", subnet3.getIp());
        assertEquals("10.0.0.0", subnet4.getIp());
    }

    @Test
    void setIpEmpty() {
        assertThrows(IllegalArgumentException.class, () -> subnet1.setIp(""));
    }

    @Test
    void setSubnetmask() {
        subnet1.setSubnetmask("255");
        subnet2.setSubnetmask(new String[]{"255"});
        subnet3.setSubnetmask(new int[]{255});
        assertEquals("255.0.0.0", subnet1.getSubnetmask());
        assertEquals("255.0.0.0", subnet2.getSubnetmask());
        assertEquals("255.0.0.0", subnet3.getSubnetmask());
    }

    @Test
    void setSubnetmaskBasedOnClass() {
        subnet1.setSubnetmaskBasedOnClass();
        subnet2.setSubnetmaskBasedOnClass();
        subnet3.setSubnetmaskBasedOnClass();
        subnet4.setSubnetmaskBasedOnClass();
        subnet5.setSubnetmaskBasedOnClass();

        assertEquals("255.0.0.0", subnet1.getSubnetmask());
        assertEquals("255.255.0.0", subnet2.getSubnetmask());
        assertEquals("255.255.255.0", subnet3.getSubnetmask());
        assertEquals("255.255.255.0", subnet4.getSubnetmask());
        assertEquals("255.255.255.0", subnet5.getSubnetmask());
    }

    @Test
    void setSubnetmaskEmpty() {
        assertThrows(IllegalArgumentException.class, () -> subnet1.setSubnetmask(""));
    }
    //endregion

    //region getter (basic)
    @Test
    void getIp() {
        assertEquals("10.0.0.0", subnet1.getIp());
        assertEquals("128.245.97.0", subnet2.getIp());
        assertEquals("192.168.50.0", subnet3.getIp());
        assertEquals("224.62.83.0", subnet4.getIp());
        assertEquals("240.136.42.0", subnet5.getIp());
    }

    @Test
    void getIpAsArray() {
        assertArrayEquals(new int[]{10, 0, 0, 0}, subnet1.getIpAsArray());
        assertArrayEquals(new int[]{128, 245, 97, 0}, subnet2.getIpAsArray());
        assertArrayEquals(new int[]{192, 168, 50, 0}, subnet3.getIpAsArray());
        assertArrayEquals(new int[]{224, 62, 83, 0}, subnet4.getIpAsArray());
        assertArrayEquals(new int[]{240, 136, 42, 0}, subnet5.getIpAsArray());
    }

    @Test
    void getSubnetmask() {
        assertEquals("255.0.0.0", subnet1.getSubnetmask());
        assertEquals("255.255.0.0", subnet2.getSubnetmask());
        assertEquals("255.255.224.0", subnet3.getSubnetmask());
        assertEquals("255.255.240.0", subnet4.getSubnetmask());
        assertEquals("255.255.255.0", subnet5.getSubnetmask());
    }

    @Test
    void getSubnetmaskAsArray() {
        assertArrayEquals(new int[]{255, 0, 0, 0}, subnet1.getSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 0, 0}, subnet2.getSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 224, 0}, subnet3.getSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 240, 0}, subnet4.getSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 255, 0}, subnet5.getSubnetmaskAsArray());
    }

    @Test
    void getWildmarkMask() {
        assertEquals("0.255.255.255", subnet1.getWildmarkMask());
        assertEquals("0.0.255.255", subnet2.getWildmarkMask());
        assertEquals("0.0.31.255", subnet3.getWildmarkMask());
        assertEquals("0.0.15.255", subnet4.getWildmarkMask());
        assertEquals("0.0.0.255", subnet5.getWildmarkMask());
    }

    @Test
    void getWildmarkMaskAsArray() {
        assertArrayEquals(new int[]{0, 255, 255, 255}, subnet1.getWildmarkMaskAsArray());
        assertArrayEquals(new int[]{0, 0, 255, 255}, subnet2.getWildmarkMaskAsArray());
        assertArrayEquals(new int[]{0, 0, 31, 255}, subnet3.getWildmarkMaskAsArray());
        assertArrayEquals(new int[]{0, 0, 15, 255}, subnet4.getWildmarkMaskAsArray());
        assertArrayEquals(new int[]{0, 0, 0, 255}, subnet5.getWildmarkMaskAsArray());
    }

    @Test
    void getIq() {
        assertEquals(1, subnet1.getIq());
        assertEquals(2, subnet2.getIq());
        assertEquals(2, subnet3.getIq());
        assertEquals(2, subnet4.getIq());
        assertEquals(3, subnet5.getIq());
    }

    @Test
    void getMagicNumber() {
        assertEquals(256, subnet1.getMagicNumber());
        assertEquals(256, subnet2.getMagicNumber());
        assertEquals(32, subnet3.getMagicNumber());
        assertEquals(16, subnet4.getMagicNumber());
        assertEquals(256, subnet5.getMagicNumber());
    }

    @Test
    void getMagicNumberMin() {
        assertEquals(0, subnet1.getMagicNumberMin());
        assertEquals(0, subnet2.getMagicNumberMin());
        assertEquals(32, subnet3.getMagicNumberMin());
        assertEquals(80, subnet4.getMagicNumberMin());
        assertEquals(0, subnet5.getMagicNumberMin());
    }

    @Test
    void getMagicNumberMax() {
        assertEquals(255, subnet1.getMagicNumberMax());
        assertEquals(255, subnet2.getMagicNumberMax());
        assertEquals(63, subnet3.getMagicNumberMax());
        assertEquals(95, subnet4.getMagicNumberMax());
        assertEquals(255, subnet5.getMagicNumberMax());
    }

    @Test
    void getSubnetId() {
        assertEquals("10.0.0.0", subnet1.getSubnetId());
        assertEquals("128.245.0.0", subnet2.getSubnetId());
        assertEquals("192.168.32.0", subnet3.getSubnetId());
        assertEquals("224.62.80.0", subnet4.getSubnetId());
        assertEquals("240.136.42.0", subnet5.getSubnetId());
    }

    @Test
    void getSubnetIdAsArray() {
        assertArrayEquals(new int[]{10, 0, 0, 0}, subnet1.getSubnetIdAsArray());
        assertArrayEquals(new int[]{128, 245, 0, 0}, subnet2.getSubnetIdAsArray());
        assertArrayEquals(new int[]{192, 168, 32, 0}, subnet3.getSubnetIdAsArray());
        assertArrayEquals(new int[]{224, 62, 80, 0}, subnet4.getSubnetIdAsArray());
        assertArrayEquals(new int[]{240, 136, 42, 0}, subnet5.getSubnetIdAsArray());
    }

    @Test
    void getFirstAvailableIp() {
        assertEquals("10.0.0.1", subnet1.getFirstAvailableIp());
        assertEquals("128.245.0.1", subnet2.getFirstAvailableIp());
        assertEquals("192.168.32.1", subnet3.getFirstAvailableIp());
        assertEquals("224.62.80.1", subnet4.getFirstAvailableIp());
        assertEquals("240.136.42.1", subnet5.getFirstAvailableIp());
    }

    @Test
    void getFirstAvailableIpAsArray() {
        assertArrayEquals(new int[]{10, 0, 0, 1}, subnet1.getFirstAvailableIpAsArray());
        assertArrayEquals(new int[]{128, 245, 0, 1}, subnet2.getFirstAvailableIpAsArray());
        assertArrayEquals(new int[]{192, 168, 32, 1}, subnet3.getFirstAvailableIpAsArray());
        assertArrayEquals(new int[]{224, 62, 80, 1}, subnet4.getFirstAvailableIpAsArray());
        assertArrayEquals(new int[]{240, 136, 42, 1}, subnet5.getFirstAvailableIpAsArray());
    }

    @Test
    void getLastAvailableIp() {
        assertEquals("10.255.255.254", subnet1.getLastAvailableIp());
        assertEquals("128.245.255.254", subnet2.getLastAvailableIp());
        assertEquals("192.168.63.254", subnet3.getLastAvailableIp());
        assertEquals("224.62.95.254", subnet4.getLastAvailableIp());
        assertEquals("240.136.42.254", subnet5.getLastAvailableIp());
    }

    @Test
    void getLastAvailableIpAsArray() {
        assertArrayEquals(new int[]{10, 255, 255, 254}, subnet1.getLastAvailableIpAsArray());
        assertArrayEquals(new int[]{128, 245, 255, 254}, subnet2.getLastAvailableIpAsArray());
        assertArrayEquals(new int[]{192, 168, 63, 254}, subnet3.getLastAvailableIpAsArray());
        assertArrayEquals(new int[]{224, 62, 95, 254}, subnet4.getLastAvailableIpAsArray());
        assertArrayEquals(new int[]{240, 136, 42, 254}, subnet5.getLastAvailableIpAsArray());
    }

    @Test
    void getBroadCastIp() {
        assertEquals("10.255.255.255", subnet1.getBroadCastIp());
        assertEquals("128.245.255.255", subnet2.getBroadCastIp());
        assertEquals("192.168.63.255", subnet3.getBroadCastIp());
        assertEquals("224.62.95.255", subnet4.getBroadCastIp());
        assertEquals("240.136.42.255", subnet5.getBroadCastIp());
    }

    @Test
    void getBroadCastIpAsArray() {
        assertArrayEquals(new int[]{10, 255, 255, 255}, subnet1.getBroadCastIpAsArray());
        assertArrayEquals(new int[]{128, 245, 255, 255}, subnet2.getBroadCastIpAsArray());
        assertArrayEquals(new int[]{192, 168, 63, 255}, subnet3.getBroadCastIpAsArray());
        assertArrayEquals(new int[]{224, 62, 95, 255}, subnet4.getBroadCastIpAsArray());
        assertArrayEquals(new int[]{240, 136, 42, 255}, subnet5.getBroadCastIpAsArray());
    }

    @Test
    void getClassId() {
        assertEquals("10.0.0.0", subnet1.getClassId());
        assertEquals("128.245.0.0", subnet2.getClassId());
        assertEquals("192.168.50.0", subnet3.getClassId());
        assertEquals("224.62.83.0", subnet4.getClassId());
        assertEquals("240.136.42.0", subnet5.getClassId());
    }

    @Test
    void getClassIdAsArray() {
        assertArrayEquals(new int[]{10, 0, 0, 0}, subnet1.getClassIdAsArray());
        assertArrayEquals(new int[]{128, 245, 0, 0}, subnet2.getClassIdAsArray());
        assertArrayEquals(new int[]{192, 168, 50, 0}, subnet3.getClassIdAsArray());
        assertArrayEquals(new int[]{224, 62, 83, 0}, subnet4.getClassIdAsArray());
        assertArrayEquals(new int[]{240, 136, 42, 0}, subnet5.getClassIdAsArray());
    }

    @Test
    void getClassSubnetmask() {
        assertEquals("255.0.0.0", subnet1.getClassSubnetmask());
        assertEquals("255.255.0.0", subnet2.getClassSubnetmask());
        assertEquals("255.255.255.0", subnet3.getClassSubnetmask());
        assertEquals("255.255.255.0", subnet4.getClassSubnetmask());
        assertEquals("255.255.255.0", subnet5.getClassSubnetmask());
    }

    @Test
    void getClassSubnetmaskAsArray() {
        assertArrayEquals(new int[]{255, 0, 0, 0}, subnet1.getClassSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 0, 0}, subnet2.getClassSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 255, 0}, subnet3.getClassSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 255, 0}, subnet4.getClassSubnetmaskAsArray());
        assertArrayEquals(new int[]{255, 255, 255, 0}, subnet5.getClassSubnetmaskAsArray());
    }

    @Test
    void getClassChar() {
        assertEquals('A', subnet1.getClassChar());
        assertEquals('B', subnet2.getClassChar());
        assertEquals('C', subnet3.getClassChar());
        assertEquals('D', subnet4.getClassChar());
        assertEquals('E', subnet5.getClassChar());
    }

    @Test
    void getNetbits() {
        assertEquals(8, subnet1.getNetbits());
        assertEquals(16, subnet2.getNetbits());
        assertEquals(19, subnet3.getNetbits());
        assertEquals(20, subnet4.getNetbits());
        assertEquals(24, subnet5.getNetbits());
    }

    @Test
    void getNetbitsString() {
        assertEquals("8", subnet1.getNetbitsString());
        assertEquals("16", subnet2.getNetbitsString());
        assertEquals("19 (24)", subnet3.getNetbitsString());
        assertEquals("20", subnet4.getNetbitsString());
        assertEquals("24", subnet5.getNetbitsString());
    }

    @Test
    void getSubnetbits() {
        assertEquals(0, subnet1.getSubnetbits());
        assertEquals(0, subnet2.getSubnetbits());
        assertEquals(0, subnet3.getSubnetbits());
        assertEquals(0, subnet4.getSubnetbits());
        assertEquals(0, subnet5.getSubnetbits());
    }

    @Test
    void getSubnetbitsString() {
        assertEquals("0", subnet1.getSubnetbitsString());
        assertEquals("0", subnet2.getSubnetbitsString());
        assertEquals("0 (-5)", subnet3.getSubnetbitsString());
        assertEquals("0", subnet4.getSubnetbitsString());
        assertEquals("0", subnet5.getSubnetbitsString());
    }

    @Test
    void getHostbits() {
        assertEquals(24, subnet1.getHostbits());
        assertEquals(16, subnet2.getHostbits());
        assertEquals(13, subnet3.getHostbits());
        assertEquals(12, subnet4.getHostbits());
        assertEquals(8, subnet5.getHostbits());
    }

    @Test
    void getHostbitsString() {
        assertEquals("24", subnet1.getHostbitsString());
        assertEquals("16", subnet2.getHostbitsString());
        assertEquals("13", subnet3.getHostbitsString());
        assertEquals("12", subnet4.getHostbitsString());
        assertEquals("8", subnet5.getHostbitsString());
    }

    @Test
    void getCountOfSubnets() {
        assertEquals(1, subnet1.getCountOfSubnets());
        assertEquals(1, subnet2.getCountOfSubnets());
        assertEquals(1, subnet3.getCountOfSubnets());
        assertEquals(1, subnet4.getCountOfSubnets());
        assertEquals(1, subnet5.getCountOfSubnets());
    }

    @Test
    void getCountOfSubnetsCalc() {
        assertEquals("2^0 = 1", subnet1.getCountOfSubnetsCalc());
        assertEquals("2^0 = 1", subnet2.getCountOfSubnetsCalc());
        assertEquals("2^0 = 1", subnet3.getCountOfSubnetsCalc());
        assertEquals("2^0 = 1", subnet4.getCountOfSubnetsCalc());
        assertEquals("2^0 = 1", subnet5.getCountOfSubnetsCalc());
    }

    @Test
    void getCountOfHosts() {
        assertEquals(16777214, subnet1.getCountOfHosts());
        assertEquals(65534, subnet2.getCountOfHosts());
        assertEquals(8190, subnet3.getCountOfHosts());
        assertEquals(4094, subnet4.getCountOfHosts());
        assertEquals(254, subnet5.getCountOfHosts());
    }

    @Test
    void getCountOfHostsCalc() {
        assertEquals("2^24-2 = 16777214", subnet1.getCountOfHostsCalc());
        assertEquals("2^16-2 = 65534", subnet2.getCountOfHostsCalc());
        assertEquals("2^13-2 = 8190", subnet3.getCountOfHostsCalc());
        assertEquals("2^12-2 = 4094", subnet4.getCountOfHostsCalc());
        assertEquals("2^8-2 = 254", subnet5.getCountOfHostsCalc());
    }

    @Test
    void isSupernetting() {
        assertFalse(subnet1.isSupernetting());
        assertFalse(subnet2.isSupernetting());
        assertTrue(subnet3.isSupernetting());
        assertFalse(subnet4.isSupernetting());
        assertFalse(subnet5.isSupernetting());
    }
    // endregion

    //region extras: summarize, subnets, contains
    @Test
    void summarize() {
        subnet2.setIp("192.168.20");
        assertEquals(new Subnet("192.168.0.0", "255.255.192.0"), subnet2.summarize(subnet3));
        assertEquals(new Subnet("192.168.50.0", "255.255.255.252"), subnet3.summarize(subnet3));
    }

    @Test
    void summarizeDifferent() {
        subnet2.setIp("192.167.20");
        assertEquals(new Subnet("192.160.0.0", "255.240.0.0"), subnet2.summarize(subnet3));
    }

    @Test
    void summarizeWithException() {
        assertThrows(IllegalArgumentException.class, () ->
            subnet1.summarize(subnet3));
        assertThrows(IllegalArgumentException.class, () ->
            subnet2.summarize(subnet3));
        // all other combinations throw also an exception
    }

    @Test
    void summarizeMultiple() {
        subnet1.setIp("192.168.0");
        subnet2.setIp("192.168.20");
        assertEquals(new Subnet("192.168.0.0", "255.255.192.0"), subnet1.summarize(subnet2, subnet3));
    }

    @Test
    void summarizeMultipleDifferent() {
        subnet1.setIp("192.167.5");
        subnet2.setIp("192.169.20");
        assertEquals(new Subnet("192.160.0.0", "255.240.0.0"), subnet1.summarize(Arrays.asList(subnet2, subnet3)));
    }

    @Test
    void getSubnets() {
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
        assertEquals(s1, subnet1.getSubnets());
        assertEquals(s2, subnet2.getSubnets());
        assertEquals(s3, subnet3.getSubnets());
        assertEquals(s4, subnet4.getSubnets());
        assertEquals(s5, subnet5.getSubnets());
    }

    @Test
    void getSubSubnets() {
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
        assertEquals(s5, subnet5.getSubSubnets());
    }

    @Test
    void isSameSubnet() {
        assertFalse(subnet1.isSameSubnet(subnet2));
        assertFalse(subnet2.isSameSubnet(subnet1));
        assertFalse(subnet1.isSameSubnet(subnet3));
        assertFalse(subnet3.isSameSubnet(subnet1));
        // all other combinations are also not the same subnet

        subnet2.setIp("192.168.20");
        subnet3.setSubnetmask("255.255");
        assertTrue(subnet2.isSameSubnet(subnet3));
    }

    @Test
    void contains() {
        assertFalse(subnet1.contains(subnet2));
        assertFalse(subnet2.contains(subnet1));
        assertFalse(subnet1.contains(subnet3));
        assertFalse(subnet3.contains(subnet1));
        // all other combinations do also not contain each other

        subnet2.setIp("192.168.20");
        assertTrue(subnet2.contains(subnet3));
        assertFalse(subnet3.contains(subnet2));
    }
    //endregion

    //region validate
    @Test
    void checkEntryAndConvertSubnetmaskWithMissingIp() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("", "255"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithMissingSubnetmask() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", ""));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithInvalidIp() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("/10", "255"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithToLargeIp() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("256", "255"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithInvalidSubnetmask() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "255,"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithToLargeSubnetmask() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "255.111111110"));
    }

    @Test
    void convertPrefixAndValidateWithMissingNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "/"));
    }

    @Test
    void convertPrefixAndValidateWithInvalidNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "/24,"));
    }

    @Test
    void convertPrefixAndValidateWithToLowNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "/1"));
    }

    @Test
    void convertPrefixAndValidateWithToHighNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "/32"));
    }

    @Test
    void convertBinarySubnetmaskToDecimalWithToLargeNumberInLastQuad() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "255.255.255.255"));
    }

    @Test
    void isSubnetOkWithWrongSubnetmaskNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "10"));
    }

    @Test
    void isSubnetOkWithInvalidSubnetmaskPatternUnequal0() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "255.240.240"));
    }

    @Test
    void isSubnetOkWithInvalidSubnetmaskPatternAfter0() {
        assertThrows(IllegalArgumentException.class, () -> new Subnet("10", "255.0.255"));
    }

    @Test
    void setMagicNumber() {
        assertThrows(IllegalArgumentException.class, () -> subnet1.setSubnetmask("128"));
    }
    //endregion

    //region convert
    @Test
    void convertBinaryToDecimal() {
        assertEquals(0b1, Subnet.convertBinaryToDecimal("1"));
        assertEquals(0b11, Subnet.convertBinaryToDecimal("11"));
        assertEquals(0b111, Subnet.convertBinaryToDecimal("111"));

        assertEquals(0b1, Subnet.convertBinaryToDecimal(1));
        assertEquals(0b11, Subnet.convertBinaryToDecimal(11));
        assertEquals(0b111, Subnet.convertBinaryToDecimal(111));
    }

    @Test
    void convertDecimalToBinary() {
        assertEquals(1, Subnet.convertDecimalToBinary("1"));
        assertEquals(11, Subnet.convertDecimalToBinary("3"));
        assertEquals(111, Subnet.convertDecimalToBinary("7"));

        assertEquals(1, Subnet.convertDecimalToBinary(0b1));
        assertEquals(11, Subnet.convertDecimalToBinary(0b11));
        assertEquals(111, Subnet.convertDecimalToBinary(0b111));
    }

    @Test
    void convertIntegerArrayToStringArray() {
        assertArrayEquals(new String[]{"0", "1", "2"}, Subnet.convertIntegerArrayToStringArray(new int[]{0, 1, 2}));
    }

    @Test
    void convertStringArrayToIntegerArray() {
        assertArrayEquals(new int[]{0, 1, 2}, Subnet.convertStringArrayToIntegerArray(new String[]{"0", "1", "2"}));
    }
    // endregion

    //region toString, compareTo, ...
    @Test
    void toStringTest() {
        assertEquals("10.0.0.0 255.0.0.0", subnet1.toString());
        assertEquals("128.245.97.0 255.255.0.0", subnet2.toString());
        assertEquals("192.168.50.0 255.255.224.0", subnet3.toString());
        assertEquals("224.62.83.0 255.255.240.0", subnet4.toString());
        assertEquals("240.136.42.0 255.255.255.0", subnet5.toString());
    }

    @Test
    void toStringDetailed() {
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
        assertEquals(s1, subnet1.toString(true));
        assertEquals(s2, subnet2.toString(true));
        assertEquals(s3, subnet3.toString(true));
        assertEquals(s4, subnet4.toString(true));
        assertEquals(s5, subnet5.toString(true));
    }

    @Test
    void copy() {
        assertEquals(subnet1.copy(), subnet1);
        assertEquals(subnet2.copy(), subnet2);
        assertEquals(subnet3.copy(), subnet3);
    }

    @Test
    void compareTo() {
        assertTrue(0 < subnet2.compareTo(subnet1));
        assertTrue(0 < subnet3.compareTo(subnet2));
        assertTrue(0 < subnet4.compareTo(subnet3));
        assertTrue(0 < subnet5.compareTo(subnet4));
    }

    @Test
    void equals() {
        assertEquals(new Subnet(subnet1.getIp(), subnet1.getSubnetmask()), subnet1);
        assertNotEquals(subnet1, subnet2);
        assertNotEquals(subnet1, subnet3);
        assertNotEquals(subnet2, subnet3);
    }

    @Test
    void equalsDeep() {
        subnet2.setIp("192.168.50.128");
        subnet2.setSubnetmask(subnet3.getSubnetmask());
        assertFalse(subnet1.equals(subnet3, true));
        assertTrue(subnet2.equals(subnet3, true));
    }

    @Test
    void hashCodeTest() {
        assertEquals(-838740488, subnet1.hashCode());
        assertEquals(-1722502892, subnet2.hashCode());
        assertEquals(753851720, subnet3.hashCode());
        assertEquals(-1887865192, subnet4.hashCode());
        assertEquals(701803096, subnet5.hashCode());
    }

    @Test
    void iterator() {
        final Set<Subnet> subnet1subnets = new TreeSet<>();
        final Set<Subnet> subnet2subnets = new TreeSet<>();
        final Set<Subnet> subnet3subnets = new TreeSet<>();
        for (Subnet subnet : subnet1) subnet1subnets.add(subnet);
        for (Subnet subnet : subnet2) subnet2subnets.add(subnet);
        for (Subnet subnet : subnet3) subnet3subnets.add(subnet);
        assertEquals(subnet1subnets, subnet1.getSubnets());
        assertEquals(subnet2subnets, subnet2.getSubnets());
        assertEquals(subnet3subnets, subnet3.getSubnets());
    }
    //endregion

    //region deprecated for removal
    @Test
    @SuppressWarnings({"removal"})
    void setIpDeprecation() {
        subnet2.setIp("10", false);
        assertEquals("10.0.0.0 255.255.0.0", subnet2.toString());
        assertEquals('B', subnet2.getClassChar());

        subnet2.setIp(new String[]{"11", "0", "0", "0"}, false);
        assertEquals("11.0.0.0 255.255.0.0", subnet2.toString());
        assertEquals('B', subnet2.getClassChar());

        subnet2.setIp(new int[]{10, 0, 0, 0}, false);
        assertEquals("10.0.0.0 255.255.0.0", subnet2.toString());
        assertEquals('B', subnet2.getClassChar());
    }

    @Test
    @SuppressWarnings("removal")
    void recalculateDeprecation() { // see also setIpDeprecation
        subnet2.setIp("10", false);
        assertEquals("10.0.0.0 255.255.0.0", subnet2.toString());
        assertEquals('B', subnet2.getClassChar());

        subnet2.recalculate();
        assertEquals("10.0.0.0 255.255.0.0", subnet2.toString());
        assertEquals('A', subnet2.getClassChar());
    }
    //endregion
}
