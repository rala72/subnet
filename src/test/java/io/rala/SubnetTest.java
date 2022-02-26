package io.rala;

import org.assertj.core.api.GenericComparableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

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
        assertThatObject(new Subnet(subnet1.getIp(), subnet1.getSubnetmask())).isEqualTo(subnet1);
        assertThatObject(new Subnet(
            subnet2.getIp().split("\\."),
            subnet2.getSubnetmask().split("\\."))
        ).isEqualTo(subnet2);
        assertThatObject(new Subnet(
            subnet3.getIpAsArray(),
            subnet3.getSubnetmaskAsArray())
        ).isEqualTo(subnet3);

        assertThatObject(new Subnet(subnet1.getIp())).isEqualTo(subnet1);
        assertThatObject(new Subnet(subnet2.getIp().split("\\."))).isEqualTo(subnet2);
        assertThatObject(new Subnet(subnet5.getIpAsArray())).isEqualTo(subnet5);
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

        assertThat(loopback).isNotNull();
        assertThatObject(new Subnet(loopback)).isEqualTo(new Subnet("127.0.0.1", "/8"));
    }
    //endregion

    //region setter
    @Test
    void setIp() {
        subnet2.setIp("10");
        subnet3.setIp(new String[]{"10"});
        subnet4.setIp(new int[]{10, 0, 0, 0});
        assertThat(subnet2.getIp()).isEqualTo("10.0.0.0");
        assertThat(subnet3.getIp()).isEqualTo("10.0.0.0");
        assertThat(subnet4.getIp()).isEqualTo("10.0.0.0");
    }

    @Test
    void setIpEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> subnet1.setIp(""));
    }

    @Test
    void setSubnetmask() {
        subnet1.setSubnetmask("255");
        subnet2.setSubnetmask(new String[]{"255"});
        subnet3.setSubnetmask(new int[]{255});
        assertThat(subnet1.getSubnetmask()).isEqualTo("255.0.0.0");
        assertThat(subnet2.getSubnetmask()).isEqualTo("255.0.0.0");
        assertThat(subnet3.getSubnetmask()).isEqualTo("255.0.0.0");
    }

    @Test
    void setSubnetmaskBasedOnClass() {
        subnet1.setSubnetmaskBasedOnClass();
        subnet2.setSubnetmaskBasedOnClass();
        subnet3.setSubnetmaskBasedOnClass();
        subnet4.setSubnetmaskBasedOnClass();
        subnet5.setSubnetmaskBasedOnClass();

        assertThat(subnet1.getSubnetmask()).isEqualTo("255.0.0.0");
        assertThat(subnet2.getSubnetmask()).isEqualTo("255.255.0.0");
        assertThat(subnet3.getSubnetmask()).isEqualTo("255.255.255.0");
        assertThat(subnet4.getSubnetmask()).isEqualTo("255.255.255.0");
        assertThat(subnet5.getSubnetmask()).isEqualTo("255.255.255.0");
    }

    @Test
    void setSubnetmaskEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> subnet1.setSubnetmask(""));
    }
    //endregion

    //region getter (basic)
    @Test
    void getIp() {
        assertThat(subnet1.getIp()).isEqualTo("10.0.0.0");
        assertThat(subnet2.getIp()).isEqualTo("128.245.97.0");
        assertThat(subnet3.getIp()).isEqualTo("192.168.50.0");
        assertThat(subnet4.getIp()).isEqualTo("224.62.83.0");
        assertThat(subnet5.getIp()).isEqualTo("240.136.42.0");
    }

    @Test
    void getIpAsArray() {
        assertThat(subnet1.getIpAsArray()).isEqualTo(new int[]{10, 0, 0, 0});
        assertThat(subnet2.getIpAsArray()).isEqualTo(new int[]{128, 245, 97, 0});
        assertThat(subnet3.getIpAsArray()).isEqualTo(new int[]{192, 168, 50, 0});
        assertThat(subnet4.getIpAsArray()).isEqualTo(new int[]{224, 62, 83, 0});
        assertThat(subnet5.getIpAsArray()).isEqualTo(new int[]{240, 136, 42, 0});
    }

    @Test
    void getSubnetmask() {
        assertThat(subnet1.getSubnetmask()).isEqualTo("255.0.0.0");
        assertThat(subnet2.getSubnetmask()).isEqualTo("255.255.0.0");
        assertThat(subnet3.getSubnetmask()).isEqualTo("255.255.224.0");
        assertThat(subnet4.getSubnetmask()).isEqualTo("255.255.240.0");
        assertThat(subnet5.getSubnetmask()).isEqualTo("255.255.255.0");
    }

    @Test
    void getSubnetmaskAsArray() {
        assertThat(subnet1.getSubnetmaskAsArray()).isEqualTo(new int[]{255, 0, 0, 0});
        assertThat(subnet2.getSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 0, 0});
        assertThat(subnet3.getSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 224, 0});
        assertThat(subnet4.getSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 240, 0});
        assertThat(subnet5.getSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 255, 0});
    }

    @Test
    void getWildmarkMask() {
        assertThat(subnet1.getWildmarkMask()).isEqualTo("0.255.255.255");
        assertThat(subnet2.getWildmarkMask()).isEqualTo("0.0.255.255");
        assertThat(subnet3.getWildmarkMask()).isEqualTo("0.0.31.255");
        assertThat(subnet4.getWildmarkMask()).isEqualTo("0.0.15.255");
        assertThat(subnet5.getWildmarkMask()).isEqualTo("0.0.0.255");
    }

    @Test
    void getWildmarkMaskAsArray() {
        assertThat(subnet1.getWildmarkMaskAsArray()).isEqualTo(new int[]{0, 255, 255, 255});
        assertThat(subnet2.getWildmarkMaskAsArray()).isEqualTo(new int[]{0, 0, 255, 255});
        assertThat(subnet3.getWildmarkMaskAsArray()).isEqualTo(new int[]{0, 0, 31, 255});
        assertThat(subnet4.getWildmarkMaskAsArray()).isEqualTo(new int[]{0, 0, 15, 255});
        assertThat(subnet5.getWildmarkMaskAsArray()).isEqualTo(new int[]{0, 0, 0, 255});
    }

    @Test
    void getIq() {
        assertThat(subnet1.getIq()).isEqualTo(1);
        assertThat(subnet2.getIq()).isEqualTo(2);
        assertThat(subnet3.getIq()).isEqualTo(2);
        assertThat(subnet4.getIq()).isEqualTo(2);
        assertThat(subnet5.getIq()).isEqualTo(3);
    }

    @Test
    void getMagicNumber() {
        assertThat(subnet1.getMagicNumber()).isEqualTo(256);
        assertThat(subnet2.getMagicNumber()).isEqualTo(256);
        assertThat(subnet3.getMagicNumber()).isEqualTo(32);
        assertThat(subnet4.getMagicNumber()).isEqualTo(16);
        assertThat(subnet5.getMagicNumber()).isEqualTo(256);
    }

    @Test
    void getMagicNumberMin() {
        assertThat(subnet1.getMagicNumberMin()).isZero();
        assertThat(subnet2.getMagicNumberMin()).isZero();
        assertThat(subnet3.getMagicNumberMin()).isEqualTo(32);
        assertThat(subnet4.getMagicNumberMin()).isEqualTo(80);
        assertThat(subnet5.getMagicNumberMin()).isZero();
    }

    @Test
    void getMagicNumberMax() {
        assertThat(subnet1.getMagicNumberMax()).isEqualTo(255);
        assertThat(subnet2.getMagicNumberMax()).isEqualTo(255);
        assertThat(subnet3.getMagicNumberMax()).isEqualTo(63);
        assertThat(subnet4.getMagicNumberMax()).isEqualTo(95);
        assertThat(subnet5.getMagicNumberMax()).isEqualTo(255);
    }

    @Test
    void getSubnetId() {
        assertThat(subnet1.getSubnetId()).isEqualTo("10.0.0.0");
        assertThat(subnet2.getSubnetId()).isEqualTo("128.245.0.0");
        assertThat(subnet3.getSubnetId()).isEqualTo("192.168.32.0");
        assertThat(subnet4.getSubnetId()).isEqualTo("224.62.80.0");
        assertThat(subnet5.getSubnetId()).isEqualTo("240.136.42.0");
    }

    @Test
    void getSubnetIdAsArray() {
        assertThat(subnet1.getSubnetIdAsArray()).isEqualTo(new int[]{10, 0, 0, 0});
        assertThat(subnet2.getSubnetIdAsArray()).isEqualTo(new int[]{128, 245, 0, 0});
        assertThat(subnet3.getSubnetIdAsArray()).isEqualTo(new int[]{192, 168, 32, 0});
        assertThat(subnet4.getSubnetIdAsArray()).isEqualTo(new int[]{224, 62, 80, 0});
        assertThat(subnet5.getSubnetIdAsArray()).isEqualTo(new int[]{240, 136, 42, 0});
    }

    @Test
    void getFirstAvailableIp() {
        assertThat(subnet1.getFirstAvailableIp()).isEqualTo("10.0.0.1");
        assertThat(subnet2.getFirstAvailableIp()).isEqualTo("128.245.0.1");
        assertThat(subnet3.getFirstAvailableIp()).isEqualTo("192.168.32.1");
        assertThat(subnet4.getFirstAvailableIp()).isEqualTo("224.62.80.1");
        assertThat(subnet5.getFirstAvailableIp()).isEqualTo("240.136.42.1");
    }

    @Test
    void getFirstAvailableIpAsArray() {
        assertThat(subnet1.getFirstAvailableIpAsArray()).isEqualTo(new int[]{10, 0, 0, 1});
        assertThat(subnet2.getFirstAvailableIpAsArray()).isEqualTo(new int[]{128, 245, 0, 1});
        assertThat(subnet3.getFirstAvailableIpAsArray()).isEqualTo(new int[]{192, 168, 32, 1});
        assertThat(subnet4.getFirstAvailableIpAsArray()).isEqualTo(new int[]{224, 62, 80, 1});
        assertThat(subnet5.getFirstAvailableIpAsArray()).isEqualTo(new int[]{240, 136, 42, 1});
    }

    @Test
    void getLastAvailableIp() {
        assertThat(subnet1.getLastAvailableIp()).isEqualTo("10.255.255.254");
        assertThat(subnet2.getLastAvailableIp()).isEqualTo("128.245.255.254");
        assertThat(subnet3.getLastAvailableIp()).isEqualTo("192.168.63.254");
        assertThat(subnet4.getLastAvailableIp()).isEqualTo("224.62.95.254");
        assertThat(subnet5.getLastAvailableIp()).isEqualTo("240.136.42.254");
    }

    @Test
    void getLastAvailableIpAsArray() {
        assertThat(subnet1.getLastAvailableIpAsArray()).isEqualTo(new int[]{10, 255, 255, 254});
        assertThat(subnet2.getLastAvailableIpAsArray()).isEqualTo(new int[]{128, 245, 255, 254});
        assertThat(subnet3.getLastAvailableIpAsArray()).isEqualTo(new int[]{192, 168, 63, 254});
        assertThat(subnet4.getLastAvailableIpAsArray()).isEqualTo(new int[]{224, 62, 95, 254});
        assertThat(subnet5.getLastAvailableIpAsArray()).isEqualTo(new int[]{240, 136, 42, 254});
    }

    @Test
    void getBroadCastIp() {
        assertThat(subnet1.getBroadCastIp()).isEqualTo("10.255.255.255");
        assertThat(subnet2.getBroadCastIp()).isEqualTo("128.245.255.255");
        assertThat(subnet3.getBroadCastIp()).isEqualTo("192.168.63.255");
        assertThat(subnet4.getBroadCastIp()).isEqualTo("224.62.95.255");
        assertThat(subnet5.getBroadCastIp()).isEqualTo("240.136.42.255");
    }

    @Test
    void getBroadCastIpAsArray() {
        assertThat(subnet1.getBroadCastIpAsArray()).isEqualTo(new int[]{10, 255, 255, 255});
        assertThat(subnet2.getBroadCastIpAsArray()).isEqualTo(new int[]{128, 245, 255, 255});
        assertThat(subnet3.getBroadCastIpAsArray()).isEqualTo(new int[]{192, 168, 63, 255});
        assertThat(subnet4.getBroadCastIpAsArray()).isEqualTo(new int[]{224, 62, 95, 255});
        assertThat(subnet5.getBroadCastIpAsArray()).isEqualTo(new int[]{240, 136, 42, 255});
    }

    @Test
    void getClassId() {
        assertThat(subnet1.getClassId()).isEqualTo("10.0.0.0");
        assertThat(subnet2.getClassId()).isEqualTo("128.245.0.0");
        assertThat(subnet3.getClassId()).isEqualTo("192.168.50.0");
        assertThat(subnet4.getClassId()).isEqualTo("224.62.83.0");
        assertThat(subnet5.getClassId()).isEqualTo("240.136.42.0");
    }

    @Test
    void getClassIdAsArray() {
        assertThat(subnet1.getClassIdAsArray()).isEqualTo(new int[]{10, 0, 0, 0});
        assertThat(subnet2.getClassIdAsArray()).isEqualTo(new int[]{128, 245, 0, 0});
        assertThat(subnet3.getClassIdAsArray()).isEqualTo(new int[]{192, 168, 50, 0});
        assertThat(subnet4.getClassIdAsArray()).isEqualTo(new int[]{224, 62, 83, 0});
        assertThat(subnet5.getClassIdAsArray()).isEqualTo(new int[]{240, 136, 42, 0});
    }

    @Test
    void getClassSubnetmask() {
        assertThat(subnet1.getClassSubnetmask()).isEqualTo("255.0.0.0");
        assertThat(subnet2.getClassSubnetmask()).isEqualTo("255.255.0.0");
        assertThat(subnet3.getClassSubnetmask()).isEqualTo("255.255.255.0");
        assertThat(subnet4.getClassSubnetmask()).isEqualTo("255.255.255.0");
        assertThat(subnet5.getClassSubnetmask()).isEqualTo("255.255.255.0");
    }

    @Test
    void getClassSubnetmaskAsArray() {
        assertThat(subnet1.getClassSubnetmaskAsArray()).isEqualTo(new int[]{255, 0, 0, 0});
        assertThat(subnet2.getClassSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 0, 0});
        assertThat(subnet3.getClassSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 255, 0});
        assertThat(subnet4.getClassSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 255, 0});
        assertThat(subnet5.getClassSubnetmaskAsArray()).isEqualTo(new int[]{255, 255, 255, 0});
    }

    @Test
    void getClassChar() {
        assertThat(subnet1.getClassChar()).isEqualTo('A');
        assertThat(subnet2.getClassChar()).isEqualTo('B');
        assertThat(subnet3.getClassChar()).isEqualTo('C');
        assertThat(subnet4.getClassChar()).isEqualTo('D');
        assertThat(subnet5.getClassChar()).isEqualTo('E');
    }

    @Test
    void getNetbits() {
        assertThat(subnet1.getNetbits()).isEqualTo(8);
        assertThat(subnet2.getNetbits()).isEqualTo(16);
        assertThat(subnet3.getNetbits()).isEqualTo(19);
        assertThat(subnet4.getNetbits()).isEqualTo(20);
        assertThat(subnet5.getNetbits()).isEqualTo(24);
    }

    @Test
    void getNetbitsString() {
        assertThat(subnet1.getNetbitsString()).isEqualTo("8");
        assertThat(subnet2.getNetbitsString()).isEqualTo("16");
        assertThat(subnet3.getNetbitsString()).isEqualTo("19 (24)");
        assertThat(subnet4.getNetbitsString()).isEqualTo("20");
        assertThat(subnet5.getNetbitsString()).isEqualTo("24");
    }

    @Test
    void getSubnetbits() {
        assertThat(subnet1.getSubnetbits()).isZero();
        assertThat(subnet2.getSubnetbits()).isZero();
        assertThat(subnet3.getSubnetbits()).isZero();
        assertThat(subnet4.getSubnetbits()).isZero();
        assertThat(subnet5.getSubnetbits()).isZero();
    }

    @Test
    void getSubnetbitsString() {
        assertThat(subnet1.getSubnetbitsString()).isEqualTo("0");
        assertThat(subnet2.getSubnetbitsString()).isEqualTo("0");
        assertThat(subnet3.getSubnetbitsString()).isEqualTo("0 (-5)");
        assertThat(subnet4.getSubnetbitsString()).isEqualTo("0");
        assertThat(subnet5.getSubnetbitsString()).isEqualTo("0");
    }

    @Test
    void getHostbits() {
        assertThat(subnet1.getHostbits()).isEqualTo(24);
        assertThat(subnet2.getHostbits()).isEqualTo(16);
        assertThat(subnet3.getHostbits()).isEqualTo(13);
        assertThat(subnet4.getHostbits()).isEqualTo(12);
        assertThat(subnet5.getHostbits()).isEqualTo(8);
    }

    @Test
    void getHostbitsString() {
        assertThat(subnet1.getHostbitsString()).isEqualTo("24");
        assertThat(subnet2.getHostbitsString()).isEqualTo("16");
        assertThat(subnet3.getHostbitsString()).isEqualTo("13");
        assertThat(subnet4.getHostbitsString()).isEqualTo("12");
        assertThat(subnet5.getHostbitsString()).isEqualTo("8");
    }

    @Test
    void getCountOfSubnets() {
        assertThat(subnet1.getCountOfSubnets()).isEqualTo(1);
        assertThat(subnet2.getCountOfSubnets()).isEqualTo(1);
        assertThat(subnet3.getCountOfSubnets()).isEqualTo(1);
        assertThat(subnet4.getCountOfSubnets()).isEqualTo(1);
        assertThat(subnet5.getCountOfSubnets()).isEqualTo(1);
    }

    @Test
    void getCountOfSubnetsCalc() {
        assertThat(subnet1.getCountOfSubnetsCalc()).isEqualTo("2^0 = 1");
        assertThat(subnet2.getCountOfSubnetsCalc()).isEqualTo("2^0 = 1");
        assertThat(subnet3.getCountOfSubnetsCalc()).isEqualTo("2^0 = 1");
        assertThat(subnet4.getCountOfSubnetsCalc()).isEqualTo("2^0 = 1");
        assertThat(subnet5.getCountOfSubnetsCalc()).isEqualTo("2^0 = 1");
    }

    @Test
    void getCountOfHosts() {
        assertThat(subnet1.getCountOfHosts()).isEqualTo(16777214);
        assertThat(subnet2.getCountOfHosts()).isEqualTo(65534);
        assertThat(subnet3.getCountOfHosts()).isEqualTo(8190);
        assertThat(subnet4.getCountOfHosts()).isEqualTo(4094);
        assertThat(subnet5.getCountOfHosts()).isEqualTo(254);
    }

    @Test
    void getCountOfHostsCalc() {
        assertThat(subnet1.getCountOfHostsCalc()).isEqualTo("2^24-2 = 16777214");
        assertThat(subnet2.getCountOfHostsCalc()).isEqualTo("2^16-2 = 65534");
        assertThat(subnet3.getCountOfHostsCalc()).isEqualTo("2^13-2 = 8190");
        assertThat(subnet4.getCountOfHostsCalc()).isEqualTo("2^12-2 = 4094");
        assertThat(subnet5.getCountOfHostsCalc()).isEqualTo("2^8-2 = 254");
    }

    @Test
    void isSupernetting() {
        assertThat(subnet1.isSupernetting()).isFalse();
        assertThat(subnet2.isSupernetting()).isFalse();
        assertThat(subnet3.isSupernetting()).isTrue();
        assertThat(subnet4.isSupernetting()).isFalse();
        assertThat(subnet5.isSupernetting()).isFalse();
    }
    // endregion

    //region extras: summarize, subnets, contains
    @Test
    void summarize() {
        subnet2.setIp("192.168.20");
        assertThatObject(subnet2.summarize(subnet3))
            .isEqualTo(new Subnet("192.168.0.0", "255.255.192.0"));
        assertThatObject(subnet3.summarize(subnet3))
            .isEqualTo(new Subnet("192.168.50.0", "255.255.255.252"));
    }

    @Test
    void summarizeDifferent() {
        subnet2.setIp("192.167.20");
        assertThatObject(subnet2.summarize(subnet3))
            .isEqualTo(new Subnet("192.160.0.0", "255.240.0.0"));
    }

    @Test
    void summarizeWithException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                subnet1.summarize(subnet3));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                subnet2.summarize(subnet3));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                subnet1.summarize(subnet2, subnet3));
        // all other combinations throw also an exception
    }

    @Test
    void summarizeMultiple() {
        subnet1.setIp("192.168.0");
        subnet2.setIp("192.168.20");
        assertThatObject(subnet1.summarize(subnet2, subnet3))
            .isEqualTo(new Subnet("192.168.0.0", "255.255.192.0"));
    }

    @Test
    void summarizeMultipleDifferent() {
        subnet1.setIp("192.167.5");
        subnet2.setIp("192.169.20");
        assertThatObject(subnet1.summarize(Arrays.asList(subnet2, subnet3)))
            .isEqualTo(new Subnet("192.160.0.0", "255.240.0.0"));
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
        final Set<Subnet> s5 = Collections.singleton(
            new Subnet("240.136.42.0", "255.255.255.0"));
        assertThat(subnet1.getSubnets()).isEqualTo(s1);
        assertThat(subnet2.getSubnets()).isEqualTo(s2);
        assertThat(subnet3.getSubnets()).isEqualTo(s3);
        assertThat(subnet4.getSubnets()).isEqualTo(s4);
        assertThat(subnet5.getSubnets()).isEqualTo(s5);
    }

    @Test
    void getSubSubnets() {
        // System.out.println(subnet1.getSubSubnets()); // takes to long
        // System.out.println(subnet2.getSubSubnets()); // takes to long
        // System.out.println(subnet3.getSubSubnets()); // takes to long
        // System.out.println(subnet4.getSubSubnets()); // takes to long
        // System.out.println(subnet5.getSubSubnets()); // acceptable - but still a long list
        subnet5.setSubnetmask("/28");
        Set<Subnet> s5 = Set.of(
            new Subnet("240.136.42.1", "255.255.255.240"),
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
            new Subnet("240.136.42.14", "255.255.255.240")
        );
        assertThat(subnet5.getSubSubnets()).isEqualTo(s5);
    }

    @Test
    void isSameSubnet() {
        assertThat(subnet1.isSameSubnet(subnet2)).isFalse();
        assertThat(subnet2.isSameSubnet(subnet1)).isFalse();
        assertThat(subnet1.isSameSubnet(subnet3)).isFalse();
        assertThat(subnet3.isSameSubnet(subnet1)).isFalse();
        // all other combinations are also not the same subnet

        subnet2.setIp("192.168.20");
        subnet3.setSubnetmask("255.255");
        assertThat(subnet2.isSameSubnet(subnet3)).isTrue();
    }

    @Test
    void contains() {
        assertThat(subnet1.contains(subnet2)).isFalse();
        assertThat(subnet2.contains(subnet1)).isFalse();
        assertThat(subnet1.contains(subnet3)).isFalse();
        assertThat(subnet3.contains(subnet1)).isFalse();
        // all other combinations do also not contain each other

        subnet2.setIp("192.168.20");
        assertThat(subnet2.contains(subnet3)).isTrue();
        assertThat(subnet3.contains(subnet2)).isFalse();
    }
    //endregion

    //region validate
    @Test
    void checkEntryAndConvertSubnetmaskWithMissingIp() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("", "255"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithMissingSubnetmask() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", ""));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithInvalidIp() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("/10", "255"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithToLargeIp() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("256", "255"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithInvalidSubnetmask() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "255,"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithToLargeBinarySubnetmask() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "255.abc"));
    }

    @Test
    void checkEntryAndConvertSubnetmaskWithToLargeSubnetmask() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "255.111111110"));
    }

    @Test
    void convertPrefixAndValidateWithMissingNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "/"));
    }

    @Test
    void convertPrefixAndValidateWithInvalidNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "/24,"));
    }

    @Test
    void convertPrefixAndValidateWithToLowNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "/1"));
    }

    @Test
    void convertPrefixAndValidateWithToHighNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "/32"));
    }

    @Test
    void convertBinarySubnetmaskToDecimalWithToLargeNumberInLastQuad() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "255.255.255.255"));
    }

    @Test
    void isSubnetOkWithWrongSubnetmaskNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "10"));
    }

    @Test
    void isSubnetOkWithInvalidSubnetmaskPatternUnequal0() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "255.240.240"));
    }

    @Test
    void isSubnetOkWithInvalidSubnetmaskPatternAfter0() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Subnet("10", "255.0.255"));
    }

    @Test
    void setMagicNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> subnet1.setSubnetmask("128"));
    }
    //endregion

    //region convert
    @Test
    void convertBinaryToDecimal() {
        assertThat(Subnet.convertBinaryToDecimal("1")).isEqualTo(0b1);
        assertThat(Subnet.convertBinaryToDecimal("11")).isEqualTo(0b11);
        assertThat(Subnet.convertBinaryToDecimal("111")).isEqualTo(0b111);

        assertThat(Subnet.convertBinaryToDecimal(1)).isEqualTo(0b1);
        assertThat(Subnet.convertBinaryToDecimal(11)).isEqualTo(0b11);
        assertThat(Subnet.convertBinaryToDecimal(111)).isEqualTo(0b111);
    }

    @Test
    void convertDecimalToBinary() {
        assertThat(Subnet.convertDecimalToBinary("1")).isEqualTo(1);
        assertThat(Subnet.convertDecimalToBinary("3")).isEqualTo(11);
        assertThat(Subnet.convertDecimalToBinary("7")).isEqualTo(111);

        assertThat(Subnet.convertDecimalToBinary(0b1)).isEqualTo(1);
        assertThat(Subnet.convertDecimalToBinary(0b11)).isEqualTo(11);
        assertThat(Subnet.convertDecimalToBinary(0b111)).isEqualTo(111);
    }

    @Test
    void convertIntegerArrayToStringArray() {
        assertThat(Subnet.convertIntegerArrayToStringArray(new int[]{0, 1, 2}))
            .isEqualTo(new String[]{"0", "1", "2"});
    }

    @Test
    void convertStringArrayToIntegerArray() {
        assertThat(Subnet.convertStringArrayToIntegerArray(new String[]{"0", "1", "2"}))
            .isEqualTo(new int[]{0, 1, 2});
    }
    // endregion

    //region toString, compareTo, ...
    @Test
    void toStringTest() {
        assertThatObject(subnet1).hasToString("10.0.0.0 255.0.0.0");
        assertThatObject(subnet2).hasToString("128.245.97.0 255.255.0.0");
        assertThatObject(subnet3).hasToString("192.168.50.0 255.255.224.0");
        assertThatObject(subnet4).hasToString("224.62.83.0 255.255.240.0");
        assertThatObject(subnet5).hasToString("240.136.42.0 255.255.255.0");
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
        assertThat(subnet1.toString(true)).isEqualTo(s1);
        assertThat(subnet2.toString(true)).isEqualTo(s2);
        assertThat(subnet3.toString(true)).isEqualTo(s3);
        assertThat(subnet4.toString(true)).isEqualTo(s4);
        assertThat(subnet5.toString(true)).isEqualTo(s5);
    }

    @Test
    void copy() {
        assertThatObject(subnet1).isEqualTo(subnet1.copy());
        assertThatObject(subnet2).isEqualTo(subnet2.copy());
        assertThatObject(subnet3).isEqualTo(subnet3.copy());
    }

    @Test
    void compareTo() {
        assertThatComparable(subnet2).isGreaterThan(subnet1);
        assertThatComparable(subnet3).isGreaterThan(subnet2);
        assertThatComparable(subnet4).isGreaterThan(subnet3);
        assertThatComparable(subnet5).isGreaterThan(subnet4);
    }

    @Test
    void equals() {
        assertThatObject(subnet1).isEqualTo(new Subnet(subnet1.getIp(), subnet1.getSubnetmask()));
        assertThatObject(subnet2).isNotEqualTo(subnet1);
        assertThatObject(subnet3).isNotEqualTo(subnet1);
        assertThatObject(subnet3).isNotEqualTo(subnet2);
    }

    @Test
    void equalsDeep() {
        subnet2.setIp("192.168.50.128");
        subnet2.setSubnetmask(subnet3.getSubnetmask());
        assertThat(subnet1.equals(subnet3, true)).isFalse();
        assertThat(subnet2.equals(subnet3, true)).isTrue();
    }

    @Test
    void hashCodeTest() {
        assertThat(subnet1.hashCode()).isEqualTo(-838740488);
        assertThat(subnet2.hashCode()).isEqualTo(-1722502892);
        assertThat(subnet3.hashCode()).isEqualTo(753851720);
        assertThat(subnet4.hashCode()).isEqualTo(-1887865192);
        assertThat(subnet5.hashCode()).isEqualTo(701803096);
    }

    @Test
    void iterator() {
        final Set<Subnet> subnet1subnets = new TreeSet<>();
        final Set<Subnet> subnet2subnets = new TreeSet<>();
        final Set<Subnet> subnet3subnets = new TreeSet<>();
        for (Subnet subnet : subnet1) subnet1subnets.add(subnet);
        for (Subnet subnet : subnet2) subnet2subnets.add(subnet);
        for (Subnet subnet : subnet3) subnet3subnets.add(subnet);
        assertThat(subnet1.getSubnets()).isEqualTo(subnet1subnets);
        assertThat(subnet2.getSubnets()).isEqualTo(subnet2subnets);
        assertThat(subnet3.getSubnets()).isEqualTo(subnet3subnets);
    }
    //endregion

    //region deprecated for removal
    @Test
    @SuppressWarnings({"removal"})
    void setIpDeprecation() {
        subnet2.setIp("10", false);
        assertThatObject(subnet2).hasToString("10.0.0.0 255.255.0.0");
        assertThat(subnet2.getClassChar()).isEqualTo('B');

        subnet2.setIp(new String[]{"11", "0", "0", "0"}, false);
        assertThatObject(subnet2).hasToString("11.0.0.0 255.255.0.0");
        assertThat(subnet2.getClassChar()).isEqualTo('B');

        subnet2.setIp(new int[]{10, 0, 0, 0}, false);
        assertThatObject(subnet2).hasToString("10.0.0.0 255.255.0.0");
        assertThat(subnet2.getClassChar()).isEqualTo('B');
    }

    @Test
    @SuppressWarnings("removal")
    void recalculateDeprecation() { // see also setIpDeprecation
        subnet2.setIp("10", false);
        assertThatObject(subnet2).hasToString("10.0.0.0 255.255.0.0");
        assertThat(subnet2.getClassChar()).isEqualTo('B');

        subnet2.recalculate();
        assertThatObject(subnet2).hasToString("10.0.0.0 255.255.0.0");
        assertThat(subnet2.getClassChar()).isEqualTo('A');
    }
    //endregion

    private static <T extends Comparable<T>> GenericComparableAssert<T> assertThatComparable(T t) {
        return new GenericComparableAssert<>(t);
    }
}
