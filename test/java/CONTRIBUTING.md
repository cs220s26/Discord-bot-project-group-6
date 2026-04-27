## Notice to anyone who wants to edit or add test files.
The project is supposed to be tested using the junit tests and doing a full round in discord, since some of the code relies on discord itself which we can't write tests for.
Writing tests for the code that relies on discord would require some pretty significant refactoring which can produce even more errors with little payoff. Most likely you would be changing so much you wouldn't actually be testing the parts you wanted to test because you needed to make a fake function to make it pass.

### Conclusion
Don't just write tests just to increase coverage, write the tests to test code that can be tested without discord, and would reduce functionality of the program if not working correctly.
