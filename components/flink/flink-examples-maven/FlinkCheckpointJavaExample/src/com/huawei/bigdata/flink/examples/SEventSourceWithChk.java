package com.huawei.bigdata.flink.examples;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SEventSourceWithChk extends RichSourceFunction<Tuple4<Long, String, String, Integer>> implements ListCheckpointed<UDFState>
{
    private Long count = 0L;
    private boolean isRunning = true;
    private String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYX0987654321";

    public void run(SourceContext<Tuple4<Long, String, String, Integer>> ctx) throws Exception
    {
        Random random = new Random();
        while (isRunning)
        {
            for (int i = 0; i < 10000; i++)
            {
                ctx.collect(Tuple4.of(random.nextLong(), "hello-" + count, alphabet, 1));
                count++;
            }
            Thread.sleep(1000);
        }
    }

    public void cancel()
    {
        isRunning = false;
    }

    public List<UDFState> snapshotState(long l, long ll) throws Exception
    {
        UDFState udfState = new UDFState();
        List<UDFState> listState = new ArrayList<UDFState>();
        udfState.setState(count);
        listState.add(udfState);

        return listState;
    }

    public void restoreState(List<UDFState> list) throws Exception
    {
        UDFState udfState = list.get(0);
        count = udfState.getState();
    }
}
