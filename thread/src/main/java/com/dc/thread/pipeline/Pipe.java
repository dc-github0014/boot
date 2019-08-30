package com.dc.thread.pipeline;

import com.dc.thread.pipeline.tools.PipeContext;

import java.util.concurrent.TimeUnit;

/**
 * 对处理阶段的抽象。
 * 负责对输入进行处理，并将输出作为下一处理阶段的输入
 * @author huzhiqiang
 *
 */
public interface Pipe<IN, OUT> {
    /**
     * 设置当前Pipe实例的下个Pipe实例
     * @param nextPipe
     */
    void setNextPipe(Pipe<?,?> nextPipe);

    /**
     * 对输入的元素进行处理，并将处理结果作为下一个Pipe实例的输入
     * @param input
     * @throws InterruptedException
     */
    void process(IN input) throws InterruptedException;

    void init(PipeContext pipeCtx);
    void shutdown(long timeout, TimeUnit unit);
}
