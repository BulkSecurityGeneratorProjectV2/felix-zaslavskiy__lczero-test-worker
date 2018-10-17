# lczero-test-worker

This is the worker program of the Lczero test project.
Will receive games to play from the and send back the completed game results.

Worker will use username and password to authenticate requests with server.

A typical game task will typically have this configuration:
* The engine version to use. (May a specific patch to test)
* The network id to use
* Parameters to use.
* Time control requirments. Long or short game.
* Maybe an opening position to start with depending on the type of test used.

The worker will have some sort of scaling algorithm to adjust the time controls to account for fact that some users need to have more GPU/CPU time then others for equivalent games across different systems.

The worker will download the required version of the engine to play with from a github URL with SSL being required and verified.

