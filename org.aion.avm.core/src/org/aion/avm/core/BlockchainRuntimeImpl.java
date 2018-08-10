package org.aion.avm.core;

import java.util.Arrays;
import java.util.List;

import org.aion.avm.api.Address;
import org.aion.avm.api.IBlockchainRuntime;
import org.aion.avm.arraywrapper.ByteArray;
import org.aion.avm.core.util.Helpers;
import org.aion.avm.internal.IHelper;
import org.aion.kernel.Block;
import org.aion.kernel.InternalTransaction;
import org.aion.kernel.KernelInterface;
import org.aion.kernel.Log;
import org.aion.kernel.Transaction;
import org.aion.kernel.TransactionContext;
import org.aion.kernel.TransactionResult;


/**
 * The implementation of IBlockchainRuntime which is appropriate for exposure as a shadow Object instance within a DApp.
 */
public class BlockchainRuntimeImpl extends org.aion.avm.shadow.java.lang.Object implements IBlockchainRuntime {
    private final KernelInterface kernel;
    private final Avm avm;
    private IHelper helper;
    private TransactionResult result;

    private Transaction tx;
    private Block block;

    public BlockchainRuntimeImpl(KernelInterface kernel, Avm avm, TransactionContext context, IHelper helper, TransactionResult result) {
        this.kernel = kernel;
        this.avm = avm;
        this.helper = helper;
        this.result = result;

        this.tx = context.getTransaction();
        this.block = context.getBlock();
    }

    @Override
    public Address avm_getSender() {
        return new Address(tx.getFrom());
    }

    @Override
    public Address avm_getAddress() {
        // TODO: handle CREATE transaction
        return new Address(tx.getTo());
    }

    @Override
    public long avm_getEnergyLimit() {
        return tx.getEnergyLimit();
    }

    @Override
    public ByteArray avm_getData() {
        if (tx.getType() == Transaction.Type.CREATE) {
            if (Helpers.decodeCodeAndData(tx.getData())[1] == null) {
                return null;
            }
            else {
                return new ByteArray(Helpers.decodeCodeAndData(tx.getData())[1]);
            }
        }
        else {
            return new ByteArray(tx.getData());
        }
    }

    @Override
    public ByteArray avm_getStorage(ByteArray key) {
        return new ByteArray(this.kernel.getStorage(tx.getTo(), key.getUnderlying()));
    }

    @Override
    public void avm_putStorage(ByteArray key, ByteArray value) {
        this.kernel.putStorage(tx.getTo(), key.getUnderlying(), value.getUnderlying());
    }

    @Override
    public void avm_updateCode(ByteArray newCode) {
        this.kernel.updateCode(tx.getTo(), newCode.getUnderlying());
    }

    @Override
    public void avm_selfDestruct(Address beneficiary) {
        this.kernel.selfdestruct(tx.getTo(), beneficiary.unwrap());
    }

    @Override
    public long avm_getBlockEpochSeconds() {
        return block.getTimestamp();
    }

    @Override
    public long avm_getBlockNumber() {
        return block.getNumber();
    }

    @Override
    public ByteArray avm_sha3(ByteArray data) {
        // For now, we just return the first 32 bytes of the input data since some tests want to call this and require 32 bytes but we eventually need a real implementation (issue-152).
        // TODO: we can implement this inside vm

        return new ByteArray(Arrays.copyOfRange(data.getUnderlying(), 0, 32));
    }

    @Override
    public ByteArray avm_call(Address targetAddress, long value, ByteArray data, long energyLimit) {
        // construct the internal transaction
        InternalTransaction internalTx = new InternalTransaction(Transaction.Type.CALL,
                tx.getTo(),
                targetAddress.unwrap(),
                value,
                data.getUnderlying(),
                energyLimit,
                tx);
        result.addInternalTransaction(internalTx);

        // execute the internal transaction
        TransactionResult newResult = this.kernel.call(this.avm, internalTx, this.block);

        // merge the results
        result.merge(newResult);

        // reset the thread-local helper instance
        IHelper.currentContractHelper.set(helper);

        // charge energy consumed
        helper.externalChargeEnergy(newResult.getEnergyUsed());

        return new ByteArray(newResult.getReturnData());
    }

    @Override
    public void avm_log(ByteArray data) {
        Log log = new Log(tx.getTo(), List.of(), data.getUnderlying());
        result.addLog(log);
    }

    @Override
    public void avm_log(ByteArray topic1, ByteArray data) {
        Log log = new Log(tx.getTo(), List.of(topic1.getUnderlying()), data.getUnderlying());
        result.addLog(log);
    }

    @Override
    public void avm_log(ByteArray topic1, ByteArray topic2, ByteArray data) {
        Log log = new Log(tx.getTo(), List.of(topic1.getUnderlying(), topic2.getUnderlying()), data.getUnderlying());
        result.addLog(log);
    }

    @Override
    public void avm_log(ByteArray topic1, ByteArray topic2, ByteArray topic3, ByteArray data) {
        Log log = new Log(tx.getTo(), List.of(topic1.getUnderlying(), topic2.getUnderlying(), topic3.getUnderlying()), data.getUnderlying());
        result.addLog(log);
    }

    @Override
    public void avm_log(ByteArray topic1, ByteArray topic2, ByteArray topic3, ByteArray topic4, ByteArray data) {
        Log log = new Log(tx.getTo(), List.of(topic1.getUnderlying(), topic2.getUnderlying(), topic3.getUnderlying(), topic4.getUnderlying()), data.getUnderlying());
        result.addLog(log);
    }
}