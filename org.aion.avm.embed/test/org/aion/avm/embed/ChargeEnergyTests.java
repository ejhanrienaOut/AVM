package org.aion.avm.embed;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.math.BigInteger;
import org.aion.avm.userlib.abi.ABIEncoder;
import org.aion.kernel.AvmWrappedTransactionResult.AvmInternalError;
import org.aion.types.AionAddress;
import org.aion.types.TransactionResult;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Tests energy chargings for methods that operate explicitly on user-defined values. The contract
 * {@link ChargeEnergyTarget} attempts to manipulate these values to charge negative amounts or
 * very large amounts and this test suite confirms that any attempts to trigger overflow will result
 * in an uncatchable exception being thrown, and any attempt to produce a negative charge will result
 * in a non-negative charge.
 */
public class ChargeEnergyTests {
    @ClassRule
    public static AvmRule avmRule = new AvmRule(false);

    private static avm.Address deployer = avmRule.getPreminedAccount();
    private static AionAddress contract;
    private static long energyLimit = 1_573_741_824L;

    @BeforeClass
    public static void setup() {
        contract = deployContract();
    }

    @Test
    public void testSystemArrayCopyNegative() {
        TransactionResult result = callSystemArrayCopyNegativeCost();
        assertTrue(result.transactionStatus.isSuccess());
        assertTrue(result.energyUsed < energyLimit);
        assertTrue(result.energyUsed > 0);
    }

    @Test
    public void testSystemArrayCopyOverflow() {
        TransactionResult result = callSystemArrayCopyOverflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringRegionMatches1Negative() {
        TransactionResult result = callStringRegionMatches1Negative();
        assertTrue(result.transactionStatus.isSuccess());
        assertTrue(result.energyUsed < energyLimit);
        assertTrue(result.energyUsed > 0);
    }

    @Test
    public void testStringRegionMatches1Overflow() {
        TransactionResult result = callStringRegionMatches1Overflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringRegionMatches2Negative() {
        TransactionResult result = callStringRegionMatches2Negative();
        assertTrue(result.transactionStatus.isSuccess());
        assertTrue(result.energyUsed < energyLimit);
        assertTrue(result.energyUsed > 0);
    }

    @Test
    public void testStringRegionMatches2Overflow() {
        TransactionResult result = callStringRegionMatches2Overflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringValueOfNegative() {
        TransactionResult result = callStringValueOfNegative();
        assertTrue(result.transactionStatus.isSuccess());
        assertTrue(result.energyUsed < energyLimit);
        assertTrue(result.energyUsed > 0);
    }

    @Test
    public void testStringValueOfOverflow() {
        TransactionResult result = callStringValueOfOverflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringCopyValueOfNegative() {
        TransactionResult result = callStringCopyValueOfNegative();
        assertTrue(result.transactionStatus.isSuccess());
        assertTrue(result.energyUsed < energyLimit);
        assertTrue(result.energyUsed > 0);
    }

    @Test
    public void testStringCopyValueOfOverflow() {
        TransactionResult result = callStringCopyValueOfOverflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringGetCharsOverflow() {
        TransactionResult result = callStringGetCharsOverflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringIndexOfOverflow() {
        TransactionResult result = callStringIndexOfOverflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringLastIndexOf1Overflow() {
        TransactionResult result = callStringLastIndexOf1Overflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringLastIndexOf2Overflow() {
        TransactionResult result = callStringLastIndexOf2Overflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringSubstring1Overflow() {
        TransactionResult result = callStringSubstring1Overflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringSubstring2Overflow() {
        TransactionResult result = callStringSubstring2Overflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }

    @Test
    public void testStringSubsequenceOverflow() {
        TransactionResult result = callStringSubsequenceOverflow();
        assertEquals(AvmInternalError.FAILED_UNEXPECTED.error, result.transactionStatus.causeOfError);
    }


    private static AionAddress deployContract() {
        byte[] jarBytes = avmRule.getDappBytes(ChargeEnergyTarget.class, null);
        AvmRule.ResultWrapper result = avmRule.deploy(deployer, BigInteger.ZERO, jarBytes, 5_000_000, 1);
        assertTrue(result.getReceiptStatus().isSuccess());
        return new AionAddress(result.getDappAddress().toByteArray());
    }

    private TransactionResult callStringSubsequenceOverflow() {
        byte[] data = ABIEncoder.encodeOneString("stringSubsequenceOverflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringSubstring1Overflow() {
        byte[] data = ABIEncoder.encodeOneString("stringSubstring1Overflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringSubstring2Overflow() {
        byte[] data = ABIEncoder.encodeOneString("stringSubstring2Overflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringLastIndexOf1Overflow() {
        byte[] data = ABIEncoder.encodeOneString("stringLastIndexOf1Overflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringLastIndexOf2Overflow() {
        byte[] data = ABIEncoder.encodeOneString("stringLastIndexOf2Overflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringIndexOfOverflow() {
        byte[] data = ABIEncoder.encodeOneString("stringIndexOfOverflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringGetCharsOverflow() {
        byte[] data = ABIEncoder.encodeOneString("stringGetCharsOverflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringCopyValueOfNegative() {
        byte[] data = ABIEncoder.encodeOneString("stringCopyValueOfNegative");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringCopyValueOfOverflow() {
        byte[] data = ABIEncoder.encodeOneString("stringCopyValueOfOverflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringValueOfNegative() {
        byte[] data = ABIEncoder.encodeOneString("stringValueOfNegative");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringValueOfOverflow() {
        byte[] data = ABIEncoder.encodeOneString("stringValueOfOverflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringRegionMatches1Negative() {
        byte[] data = ABIEncoder.encodeOneString("stringRegionMatches1Negative");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringRegionMatches1Overflow() {
        byte[] data = ABIEncoder.encodeOneString("stringRegionMatches1Overflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringRegionMatches2Negative() {
        byte[] data = ABIEncoder.encodeOneString("stringRegionMatches2Negative");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callStringRegionMatches2Overflow() {
        byte[] data = ABIEncoder.encodeOneString("stringRegionMatches2Overflow");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callSystemArrayCopyNegativeCost() {
        byte[] data = ABIEncoder.encodeOneString("systemArrayCopyNegative");
        return makeCall(data).getTransactionResult();
    }

    private TransactionResult callSystemArrayCopyOverflow() {
        byte[] data = ABIEncoder.encodeOneString("systemArrayCopyOverflow");
        return makeCall(data).getTransactionResult();
    }

    private AvmRule.ResultWrapper makeCall(byte[] data) {
        avm.Address contractAddress = new avm.Address(contract.toByteArray());
        return avmRule.call(deployer, contractAddress, BigInteger.ZERO, data, energyLimit, 1);
    }
}
