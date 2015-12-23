#!/usr/bin/env python

# Copyright (c) Twisted Matrix Laboratories.
# See LICENSE for details.

from twisted.internet.protocol import Protocol, Factory
from twisted.internet import reactor

### Protocol Implementation

# This is just about the simplest possible protocol
class Echo(Protocol):
    def connectionMade(self):
        print "Client connected!"

    def dataReceived(self, data):
        """
        As soon as any data is received, write it back.
        """
        print "Got data: "+data
        self.transport.write(data)


def main():
    f = Factory()
    f.protocol = Echo
    reactor.listenTCP(5666, f)
    reactor.run()

if __name__ == '__main__':
    main()
