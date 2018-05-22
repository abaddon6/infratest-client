# test execution for given host
mvn test -Dhost=[HOST_ADDRESS]

# other options:
# asserts - if true then constrains will be checked
# repeat - how many times tests should be repeated
# writeToFile - write results to results.txt file
# noWriteToOutput - disable writing results to output

# example
mvn test -Dhost=http://localhost:8080 -Drepeat=2 -Dasserts