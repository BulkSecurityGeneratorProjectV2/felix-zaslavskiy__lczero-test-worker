# lczero-test-worker

This is the worker program of the Lczero test project.
Will receive games to play from the and send back the completed game results.

Worker will use username and password to authenticate requests with server.

A typical game task will typically have this configuration:
* The engine version to use. (May a specific patch to test)
* The network id to use
* Parameters to use.
* Time control requirements.
* Maybe an opening position to start with depending on the type of test used.

The worker will have some sort of scaling algorithm to adjust the time controls to account for fact that some users need to have more GPU/CPU time then others for equivalent games across different systems.

The worker will download the required version of the engine to play with from a github URL with SSL being required and verified.

Dev plans:

- Get to a simplest possible working implementation (MVP)
- Cleanup lc0 binaries on start.

- Add signature verification to requests. Some sort of token validation that games played are once received.

- Run a benchmark using Opening book and cutechess. Find scaling factor.
- How to detect the type of hardware support that is available?


# Commands that worked:
cutechess.exe -games 1 -engine cmd=lc0-v0.18.1-windows-blas\lc0.exe arg="--debuglog=t2.txt" -engine cmd=lc0-v0.18.1-windows-blas\lc0.exe arg="--debuglog=t1.txt" -each proto=uci tc=40/60+2 arg="--weights=n16b6e505904aac83d965a35fb2367819d613dc73328d900129f4b43b6d986db60" dir=lc0-v0.18.1-windows-blas -debug

This one run to a draw.
cutechess.exe -games 1 -engine cmd=lc0-v0.18.1-windows-blas\lc0.exe  -engine cmd=lc0-v0.18.1-windows-blas\lc0.exe -each proto=uci tc=30+5 arg="--weights=n16b6e505904aac83d965a35fb2367819d613dc73328d900129f4b43b6d986db60" dir=lc0-v0.18.1-windows-blas -debug -wait 200 -pgnout game.pgn