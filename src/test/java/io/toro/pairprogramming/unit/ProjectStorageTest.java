package io.toro.pairprogramming.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public abstract class ProjectStorageTest {

    @Test
    public abstract void shouldWriteToFile() throws Exception;

    @Test
    public abstract void shouldMoveFile() throws Exception;

    @Test
    public abstract void shouldCopyFile() throws Exception;

    @Test
    public abstract void shouldReadFile() throws Exception;

    @Test
    public abstract void shouldDeleteFile() throws Exception;

    @Test
    public abstract void shouldCreateDirectory() throws Exception;

    @Test
    public abstract void shouldMoveDirectory() throws Exception;

    @Test
    public abstract void shouldCopyDirectory() throws Exception;

    @Test
    public abstract void shouldReadDirectory() throws Exception;

    @Test
    public abstract void shouldDeleteDirectory() throws Exception;
}
