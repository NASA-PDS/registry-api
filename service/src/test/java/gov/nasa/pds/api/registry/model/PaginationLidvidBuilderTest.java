package gov.nasa.pds.api.registry.model;

import gov.nasa.pds.api.registry.LidvidsContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PaginationLidvidBuilderTest {

    private final List<String> simpleInput = Arrays.asList("1", "2", "3", "4", "5");
    private final List<List<String>> complexInput = Arrays.asList(
            Arrays.asList("1", "2", "3"),
            Arrays.asList("4", "5", "6"),
            Arrays.asList("7", "8", "9")
    );

    @Test
    public void testSimpleAddTrimNothing() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 5));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, pageBuilder.page().toArray());
    }

    @Test
    public void testSimpleAddTrimEverything() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 0));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{}, pageBuilder.page().toArray());
    }

    @Test
    public void testSimpleAddTrimHead() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(2, 3));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{"3", "4", "5"}, pageBuilder.page().toArray());
    }

    @Test
    public void testSimpleAddTrimHeadWithExtraData() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(2, 2));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{"3", "4"}, pageBuilder.page().toArray());
    }

    @Test
    public void testSimpleAddTrimHeadWithInsufficientData() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(2, 100));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{"3", "4", "5"}, pageBuilder.page().toArray());
    }

    @Test
    public void testSimpleAddTrimTail() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 3));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{"1", "2", "3"}, pageBuilder.page().toArray());
    }

    @Test
    public void testSimpleAddTrimHeadAndTail() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 3));
        pageBuilder.addAll(simpleInput);
        Assert.assertArrayEquals(new String[]{"2", "3", "4"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimNothing() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 9));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimEverything1() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 0));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimEverything2() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(4, 0));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimEverything3() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(7, 0));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHead1() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 8));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"2", "3", "4", "5", "6", "7", "8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHead2() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(4, 5));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"5", "6", "7", "8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHead3() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(7, 2));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHeadWithExtraData1() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 2));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"2", "3"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHeadWithExtraData2() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 5));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"2", "3", "4", "5", "6"}, pageBuilder.page().toArray());
    }


    @Test
    public void testComplexAddTrimHead1InsufficientData() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 100));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"2", "3", "4", "5", "6", "7", "8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHead2InsufficientData() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(4, 100));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"5", "6", "7", "8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHead3InsufficientData() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(7, 100));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"8", "9"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimTail1() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 8));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimTail2() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 5));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimTail3() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(0, 2));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"1", "2"}, pageBuilder.page().toArray());
    }

    @Test
    public void testComplexAddTrimHeadAndTail1() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(1, 1));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"2"}, pageBuilder.page().toArray());
    }


    @Test
    public void testComplexAddTrimHeadAndTail2() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(4, 1));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"5"}, pageBuilder.page().toArray());
    }


    @Test
    public void testComplexAddTrimHeadAndTail3() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(7, 1));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"8"}, pageBuilder.page().toArray());
    }


    @Test
    public void testComplexAddTrimHeadAndTailSpanning() {
        PaginationLidvidBuilder pageBuilder = new PaginationLidvidBuilder(new LidvidsContextStub(2, 5));
        for (List<String> input : complexInput) {
            pageBuilder.addAll(input);
        }
        Assert.assertArrayEquals(new String[]{"3", "4", "5", "6", "7"}, pageBuilder.page().toArray());
    }
}


class LidvidsContextStub implements LidvidsContext {
    private final int start;
    private final int limit;

    public LidvidsContextStub(int start, int limit) {
        this.start = start;
        this.limit = limit;
    }

    @Override
    public String getLidVid() {
        return null;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Integer getStart() {
        return start;
    }
}
