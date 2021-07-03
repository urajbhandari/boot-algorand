# How to use Spring Framework with Java Algorand SDK
Simple Algorand app that can be used to transfer ALGOs between two accounts.

## How to run
  - Start a private Algorand network: ./sandbox up
  - Transfer 100 ALGOs (equivalent to 100000000 micro ALGOs) to alice's account using the following command line command:
    ./sandbox goal clerk send -a 100000000 -f KWW3LGTW4AVPWEUUERQ4IN2OMCA2WZPEZSACB2HV774U5C5HV6XBLTTXEM -t 634XGEIC3YHBMEO5P4N7XDUQ3VC7U6MQGG3C5MYEQO5O7WFRBY757PHLKQ
  - Run AlgoTransferServiceTest.testTransferAlgoFromAliceToBob() test method that transfers 1ALGO from bob to alice.
