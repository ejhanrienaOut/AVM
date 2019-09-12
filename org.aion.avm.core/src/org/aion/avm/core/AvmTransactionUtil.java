package org.aion.avm.core;

import java.math.BigInteger;
import org.aion.types.InternalTransaction;
import org.aion.types.AionAddress;
import org.aion.types.Transaction;

public class AvmTransactionUtil {

    /**
     * Factory method to create a 'call' Transaction.
     */
    public static Transaction call(AionAddress sender, AionAddress destination, BigInteger nonce, BigInteger value, byte[] data, long energyLimit, long energyPrice) {
        return Transaction.contractCallTransaction(sender
            , destination
            , new byte[32]
            , nonce
            , value
            , data
            , energyLimit
            , energyPrice
        );
    }

    /**
     * Factory method to create a 'call' Transaction and to set a specific transaction hash.
     */
    public static Transaction callWithHash(AionAddress sender, AionAddress destination, byte[] hash, BigInteger nonce, BigInteger value, byte[] data, long energyLimit, long energyPrice) {
        return Transaction.contractCallTransaction(sender, destination, hash, nonce, value, data, energyLimit, energyPrice);
    }

    /**
     * Factory method to create a 'create' Transaction.
     */
    public static Transaction create(AionAddress sender, BigInteger nonce, BigInteger value, byte[] data, long energyLimit, long energyPrice) {
        return Transaction.contractCreateTransaction(sender
            , new byte[32]
            , nonce
            , value
            , data
            , energyLimit
            , energyPrice
        );
    }

    /**
     * Factory method to create a 'create' Transaction and to set a specific transaction hash.
     */
    public static Transaction createWithHash(AionAddress sender, byte[] hash, BigInteger nonce, BigInteger value, byte[] data, long energyLimit, long energyPrice) {
        return Transaction.contractCreateTransaction(sender, hash, nonce, value, data, energyLimit, energyPrice);
    }

    /**
     * Factory method to create the Transaction data type from an InternalTransaction.
     *
     * @param internalTransaction The transaction we were given.
     * @param originHash The hash of the origin or external transaction that eventually spawned this transaction.
     * @return The new Transaction instance.
     * @throws IllegalArgumentException If any elements of external are statically invalid.
     */
    public static Transaction fromInternalTransaction(InternalTransaction internalTransaction, byte[] originHash) {
        if (internalTransaction.isCreate) {
            return Transaction.contractCreateTransaction(internalTransaction.sender
                    , originHash
                    , internalTransaction.senderNonce
                    , internalTransaction.value
                    , internalTransaction.copyOfData()
                    , internalTransaction.energyLimit
                    , internalTransaction.energyPrice
            );
        } else {
            return Transaction.contractCallTransaction(internalTransaction.sender
                    , internalTransaction.destination
                    , originHash
                    , internalTransaction.senderNonce
                    , internalTransaction.value
                    , internalTransaction.copyOfData()
                    , internalTransaction.energyLimit
                    , internalTransaction.energyPrice
            );
        }
    }
}
