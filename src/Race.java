public class Race {
    private TwoThreeTree<Runner> IDTree;
    private TwoThreeTree<Runner> MinTimeTree;
    private TwoThreeTree<Runner> AvgTimeTree;

    public Race()
    {
        init();
    }

    public void init()
    {
        IDTree = new TwoThreeTree<Runner>("ID");
        MinTimeTree = new TwoThreeTree<Runner>("MinTime");
        AvgTimeTree = new TwoThreeTree<Runner>("AvgTime");
    }

    public void addRunner(RunnerID id)
    {
        boolean isEmpty = false;

        //checks if the tree is empty
        if (IDTree.getRoot().getLeft().isMinNode(IDTree.MIN_SENTINEL) && IDTree.getRoot().getMiddle().isMaxNode(IDTree.MAX_SENTINEL)) {
            isEmpty = true;
        }

        //check if the runner is already exists
        if (!isEmpty && IDTree.search23(IDTree.getRoot(), new Runner(id)) != null) {
            throw new IllegalArgumentException("Runner with ID " + id + " already exists.");
        }

        IDTree.insert23(new Runner(id));
        IDTree.setSize(IDTree.getSize() + 1);
        int newSize = IDTree.getSize();
        IDTree.getRoot().setSize(newSize);
        MinTimeTree.insert23(new Runner(id));
        MinTimeTree.setSize(MinTimeTree.getSize() + 1);
        MinTimeTree.getRoot().setSize(newSize);
        AvgTimeTree.insert23(new Runner(id));
        AvgTimeTree.setSize(AvgTimeTree.getSize() + 1);
        AvgTimeTree.getRoot().setSize(newSize);
        if (!isEmpty) {
            IDTree.getRoot().setLeaf(false);
            MinTimeTree.getRoot().setLeaf(false);
            AvgTimeTree.getRoot().setLeaf(false);
        }
    }

    public void removeRunner(RunnerID id)
    {
        boolean isEmpty = false;

        //checks if the tree is empty
        if (IDTree.getRoot().getLeft().isMinNode(IDTree.MIN_SENTINEL) && IDTree.getRoot().getMiddle().isMaxNode(IDTree.MAX_SENTINEL)) {
            isEmpty = true;
        }

        //check if the runner is already exists
        if (!isEmpty && IDTree.search23(IDTree.getRoot(), new Runner(id)) == null) {
            throw new IllegalArgumentException("Runner with ID " + id + " already exists.");
        }

        IDTree.delete23(IDTree.search23(IDTree.getRoot(), new Runner(id)).getKey(), null, false);
        IDTree.setSize(IDTree.getSize() - 1);
        int newSize = IDTree.getSize();
        IDTree.getRoot().setSize(newSize);
        MinTimeTree.delete23(MinTimeTree.search23(MinTimeTree.getRoot(), new Runner(id)).getKey(), null, false);
        MinTimeTree.setSize(MinTimeTree.getSize() - 1);
        MinTimeTree.getRoot().setSize(newSize);
        AvgTimeTree.delete23(AvgTimeTree.search23(AvgTimeTree.getRoot(), new Runner(id)).getKey(), null, false);
        AvgTimeTree.setSize(AvgTimeTree.getSize() - 1);
        AvgTimeTree.getRoot().setSize(newSize);

    }

    //Version 2
    public void addRunToRunner(RunnerID id, float time) {
        // Search for the runner in the IDTree
        Node<Runner> searchResult = IDTree.search23(IDTree.getRoot(), new Runner(id));
        if (searchResult == null || searchResult.getKey() == null) {
            // Handle the case where the runner does not exist in the tree
            throw new IllegalArgumentException("Runner with ID " + id + " not found.");
        }

        // Get the runner from the search result
        Runner runnerID = searchResult.getKey();
        Node<Runner> searchResultMinTime = MinTimeTree.search23(MinTimeTree.getRoot(), runnerID);
        Runner runnerMimTime = searchResultMinTime.getKey();
        Node<Runner> searchResultAvgTime = AvgTimeTree.search23(AvgTimeTree.getRoot(), runnerID);
        Runner runnerAvgTime = searchResultAvgTime.getKey();

        // Insert the new run time into the Runs TwoThreeTree of the runner
        runnerID.getRuns().insert23(new Run(time, id)); // Assuming Run has a constructor Run(float time, RunnerID id)
        runnerID.getRuns().setSize(runnerID.getRuns().getRoot().getSize() + 1);
        int newSize = runnerID.getRuns().getSize();
        runnerID.getRuns().getRoot().setSize(newSize);

        runnerMimTime.getRuns().insert23(new Run(time, id));
        runnerMimTime.getRuns().setSize(runnerMimTime.getRuns().getRoot().getSize() + 1);
        runnerMimTime.getRuns().getRoot().setSize(newSize);

        runnerAvgTime.getRuns().insert23(new Run(time, id));
        runnerAvgTime.getRuns().setSize(runnerAvgTime.getRuns().getRoot().getSize() + 1);
        runnerAvgTime.getRuns().getRoot().setSize(newSize);

        // Update min time if necessary
        if (runnerID.getMinTime() > time) {
            runnerID.setMinTime(time);
            runnerMimTime.setMinTime(time);
            runnerAvgTime.setMinTime(time);
        }

        // Update average time
        // The size is incremented within the insert23 method, so get the updated size after insertion
        int numberOfRuns = runnerID.getRuns().getRoot().getSize();
        float totalTimes = runnerID.getAvgTime() * (numberOfRuns - 1) + time;
        float newAvg = numberOfRuns > 0 ? totalTimes / numberOfRuns : 0;
        runnerID.setAvgTime(newAvg);
        runnerMimTime.setAvgTime(newAvg);
        runnerAvgTime.setAvgTime(newAvg);

        // Update MinTimeTree and AvgTimeTree if they exist and are not null
        if (MinTimeTree != null || AvgTimeTree != null) {
            // Assume delete23 and insert23 handle re-balancing and re-ordering the tree;
            IDTree.delete23(runnerID, null, false);
            IDTree.insert23(runnerID);
            IDTree.getRoot().setSize(IDTree.getSize());

            MinTimeTree.delete23(runnerMimTime, searchResultMinTime, true);
            MinTimeTree.insert23(runnerMimTime);
            MinTimeTree.getRoot().setSize(MinTimeTree.getSize());

            AvgTimeTree.delete23(runnerAvgTime, searchResultAvgTime, true);
            AvgTimeTree.insert23(runnerAvgTime);
            AvgTimeTree.getRoot().setSize(AvgTimeTree.getSize());
        }
    }
    //Version 1
    /*
    public void addRunToRunner(RunnerID id, float time)
    {
        Node<Runner> searchResult = IDTree.search23(IDTree.getRoot(), new Runner(id));
        if (searchResult == null || searchResult.getKey() == null) {
            // Handle the case where the runner does not exist in the tree
            System.out.println("Runner with ID " + id + " not found.");
            return;
        }
        Runner runner = searchResult.getKey();

        if (runner.getRuns() == null) {
            // Initialize Runs TwoThreeTree if it's null
            runner.setRuns(new TwoThreeTree<>());
        }

        runner.getRuns().insert23(new Node<>(new Run(time, id)));

        if (runner.getMinTime() == 0) {
            runner.setMinTime(time);
        } else if(runner.getMinTime() > time) {
            runner.setMinTime(time);
        }

        float newAvg = ((runner.getAvgTime() * runner.getRuns().getSize()) + time)/(runner.getRuns().getSize() + 1);
        if (runner.getAvgTime() == 0) {
            runner.setAvgTime(time);
        }
        runner.setAvgTime(newAvg);

        runner.getRuns().setSize(runner.getRuns().getSize() + 1);

        //TODO: check if the update MinTimeTree and AvgTimeTree is valid
    }
     */

    public void removeRunFromRunner(RunnerID id, float time)
    {
        //TODO: check if the runner exists AND the run exists
        Node<Runner> searchResult = IDTree.search23(IDTree.getRoot(), new Runner(id));
        if (searchResult == null) {
            throw new IllegalArgumentException("Runner with ID " + id + " not found.");
        }
        Node<Run> searchResultRun = searchResult.getKey().getRuns().search23(searchResult.getKey().getRuns().getRoot(), new Run(time, id));
        if (searchResultRun == null) {
            throw new IllegalArgumentException("Run with time " + time + " not found for Runner " + id + ".");
        }


        // Get the runner from the search result
        Runner runnerID = searchResult.getKey();
        Node<Runner> searchResultMinTime = MinTimeTree.search23(MinTimeTree.getRoot(), runnerID);
        Runner runnerMimTime = searchResultMinTime.getKey();
        Node<Run> searchResultRunMinTime = runnerMimTime.getRuns().search23(runnerMimTime.getRuns().getRoot(), searchResultRun.getKey());
        Node<Runner> searchResultAvgTime = AvgTimeTree.search23(AvgTimeTree.getRoot(), runnerID);
        Runner runnerAvgTime = searchResultAvgTime.getKey();
        Node<Run> searchResultRunAvgTime = runnerAvgTime.getRuns().search23(runnerAvgTime.getRuns().getRoot(), searchResultRun.getKey());

        runnerID.getRuns().delete23(searchResultRun.getKey(), searchResultRun, true);
        runnerID.getRuns().getRoot().setSize(runnerID.getRuns().getRoot().getSize() - 1);
        int newSize = runnerID.getRuns().getSize();
        runnerID.getRuns().getRoot().setSize(newSize);

        runnerMimTime.getRuns().delete23(searchResultRun.getKey(), searchResultRunMinTime, true);
        runnerMimTime.getRuns().setSize(runnerMimTime.getRuns().getSize() - 1);
        runnerMimTime.getRuns().getRoot().setSize(newSize);

        runnerAvgTime.getRuns().delete23(searchResultRun.getKey(), searchResultRunAvgTime, true);
        runnerAvgTime.getRuns().setSize(runnerAvgTime.getRuns().getSize() - 1);
        runnerAvgTime.getRuns().getRoot().setSize(newSize);

        if (runnerID.getMinTime() == time) {
            float newMinTime = runnerID.getRuns().getFastestRunner().getKey().getTime();
            runnerID.setMinTime(newMinTime);
            runnerMimTime.setMinTime(newMinTime);
            runnerAvgTime.setMinTime(newMinTime);
        }

        // Update average time
        // The size is decreased within the delete23 method, so get the updated size after deletion
        int numberOfRuns = runnerID.getRuns().getRoot().getSize();
        float totalTimes = runnerID.getAvgTime() * (numberOfRuns + 1) - time;
        float newAvg = numberOfRuns > 0 ? totalTimes / numberOfRuns : 0;
        runnerID.setAvgTime(newAvg);
        runnerMimTime.setAvgTime(newAvg);
        runnerAvgTime.setAvgTime(newAvg);

        // Update MinTimeTree and AvgTimeTree if they exist and are not null
        if (MinTimeTree != null || AvgTimeTree != null) {
            // Assume delete23 and insert23 handle re-balancing and re-ordering the tree;
            IDTree.delete23(runnerID, null, false);
            IDTree.insert23(runnerID);
            IDTree.getRoot().setSize(IDTree.getSize());

            MinTimeTree.delete23(runnerMimTime, searchResultMinTime, true);
            MinTimeTree.insert23(runnerMimTime);
            MinTimeTree.getRoot().setSize(MinTimeTree.getSize());

            AvgTimeTree.delete23(runnerAvgTime, searchResultAvgTime, true);
            AvgTimeTree.insert23(runnerAvgTime);
            AvgTimeTree.getRoot().setSize(AvgTimeTree.getSize());
        }
    }

    public RunnerID getFastestRunnerAvg()
    {
        //checks if the tree is empty
        if (IDTree.getRoot().getLeft().isMinNode(IDTree.MIN_SENTINEL) && IDTree.getRoot().getMiddle().isMaxNode(IDTree.MAX_SENTINEL)) {
            throw new IllegalArgumentException("No runners have been added yet.");
        } else {
            return AvgTimeTree.getFastestRunner().getKey().getID();
        }
    }

    public RunnerID getFastestRunnerMin()
    {
        //checks if the tree is empty
        if (IDTree.getRoot().getLeft().isMinNode(IDTree.MIN_SENTINEL) && IDTree.getRoot().getMiddle().isMaxNode(IDTree.MAX_SENTINEL)) {
            throw new IllegalArgumentException("No runners have been added yet.");
        } else {
            return MinTimeTree.getFastestRunner().getKey().getID();
        }
    }

    public float getMinRun(RunnerID id)
    {
        Node<Runner> runnerNode = IDTree.search23(IDTree.getRoot(), new Runner(id));
        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id + " not found.");
        }

        Node<Run> runRoot = runnerNode.getKey().getRuns().getRoot();
        if (runRoot.getLeft().isMinNode(runnerNode.getKey().getRuns().MIN_SENTINEL) && runRoot.getMiddle().isMaxNode(runnerNode.getKey().getRuns().MAX_SENTINEL)) {
            throw new IllegalArgumentException("No runs have been added for runner with ID " + id + ".");
        }

        return runnerNode.getKey().getMinTime();
    }

    public float getAvgRun(RunnerID id){
        Node<Runner> runnerNode = IDTree.search23(IDTree.getRoot(), new Runner(id));
        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id + " not found.");
        }

        Node<Run> runRoot = runnerNode.getKey().getRuns().getRoot();
        if (runRoot.getLeft().isMinNode(runnerNode.getKey().getRuns().MIN_SENTINEL) && runRoot.getMiddle().isMaxNode(runnerNode.getKey().getRuns().MAX_SENTINEL)) {
            throw new IllegalArgumentException("No runs have been added for runner with ID " + id + ".");
        }

        return runnerNode.getKey().getAvgTime();
    }

    public int getRankAvg(RunnerID id)
    {
        // Implement a search method to find the node with the given id
        Node<Runner> node = AvgTimeTree.search23(AvgTimeTree.getRoot(), new Runner(id));
        if (node != null) {
            return AvgTimeTree.Rank(node);
        } else {
            // Handle the case where the runner is not found
            throw new IllegalArgumentException("Runner with ID " + id + " not found.");
        }
    }

    public int getRankMin(RunnerID id)
    {
        // Implement a search method to find the node with the given id

        Node<Runner> node = MinTimeTree.search23(MinTimeTree.getRoot(), new Runner(id));
        if (node != null) {
            return MinTimeTree.Rank(node);
        } else {
            // Handle the case where the runner is not found
            throw new IllegalArgumentException("Runner with ID " + id + " not found.");
        }
    }
}
