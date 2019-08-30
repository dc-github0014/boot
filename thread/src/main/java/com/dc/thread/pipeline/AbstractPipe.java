package com.dc.thread.pipeline;

import com.dc.thread.pipeline.tools.PipeContext;
import com.dc.thread.pipeline.tools.PipeException;

import java.util.concurrent.TimeUnit;

public abstract class AbstractPipe<IN, OUT> implements Pipe<IN, OUT> {
    protected volatile Pipe<?, ?> nextPipe = null;
    protected volatile PipeContext PipeCtx = null;

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        this.nextPipe = nextPipe;
    }

    @Override
    public void process(IN input) throws InterruptedException {
        try {
            OUT out = doProcess(input);
            if(null != nextPipe){
                if(null != out){
                    ((Pipe<OUT, ?>) nextPipe).process(out);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (PipeException e) {
            PipeContext.handleError(e);
        }

    }

    @Override
    public void init(PipeContext pipeCtx) {
        this.PipeCtx = pipeCtx;
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        //什么也不做
    }

    /**
     * 留给子类实现，用于子类实现其任务处理逻辑
     */
    public abstract OUT doProcess(IN input) throws PipeException;
}