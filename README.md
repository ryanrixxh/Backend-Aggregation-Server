# ds_assignment2
This is the Assignment 2 repo


# Content Server process:
- Content Server takes the input and uses ROME to generate a feed
- Content Server serializes this feed and sends it to the Atom Server
- Atom Server will then unserialize and add the entries to the Atom Feed. Each PUT reqeust from a content server will contain a host which can be used to identify which Content Server the entries came from (in order to delete them if needed)

# Client process
- Client sends a GET request 
- Atom Server seralializes the feed once again and sends it through a socket to the Client
- Client unserializes it, formats it using JDOM Parser and displays it on the console
