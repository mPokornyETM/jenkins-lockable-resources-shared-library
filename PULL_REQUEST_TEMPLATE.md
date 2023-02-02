<!-- Comment:
A great PR typically begins with the line below.
-->

See #XXXXX
<!-- in case this PR solves Github issue use close #### or closes, closed, fix, fixes, fixed, resolve, resolves, resolved -->

<!-- Comment:
If the issue is not fully described in Github, add more information here (justification, pull request links, etc.).

 * We do not require Github issues for minor improvements.
 * Bug fixes should have a Github issue to facilitate the backporting process.
 * Major new features should have a Github issue.
-->

### Testing done

<!-- Comment:
Provide a clear description of how this change was tested.
At minimum this should include proof that a computer has executed the changed lines.
Ideally this should include an automated test or an explanation as to why this change has no tests.
Note that automated test coverage is less than complete, so a successful PR build does not necessarily imply that a computer has executed the changed lines.
If automated test coverage does not exist for the lines you are changing, **you must describe** the scenario(s) in which you manually tested the change.
For frontend changes, include screenshots of the relevant page(s) before and after the change.
For refactoring and code cleanup changes, exercise the code before and after the change and verify the behavior remains the same.
-->

### Proposed upgrade guidelines

N/A

### Submitter checklist

- [ ] The Github issue, if it exists, is well-described.
- [ ] The changelog entries and upgrade guidelines are appropriate for the audience affected by the change (users or developers, depending on the change) and are in the imperative mood (see [examples](https://github.com/jenkins-infra/jenkins.io/blob/master/content/_data/changelogs/weekly.yml)).
  - The changelog generator for plugins uses the **pull request title as the changelog entry**.
  - Fill in the **Proposed upgrade guidelines** section only if there are breaking changes or changes that may require extra steps from users during the upgrade.
<!-- Comment: TBD
- [ ] There is automated testing or an explanation that explains why this change has no tests.
- [ ] New public functions for internal use only are annotated with `@NoExternalUse`.
-->
<!-- Comment:
This steps need additional automation in release management. Therefore are commented out for now.
- [ ] New public classes, fields, and methods are annotated with `@Restricted` or have `@since TODO` Javadocs, as appropriate.
- [ ] New deprecations are annotated with `@Deprecated(since = "TODO")` or `@Deprecated(forRemoval = true, since = "TODO")`, if applicable.
-->
- [ ] For dependency updates, there are links to external changelogs and, if possible, full differentials.
- [ ] Changes in the interface are documented also as [examples](doc/examples/readme.md).

### Maintainer checklist

Before the changes are marked as `ready-for-merge`:

- [ ] There is at least one (1) approval for the pull request and no outstanding requests for change.
- [ ] Conversations in the pull request are over, or it is explicit that a reviewer is not blocking the change.
- [ ] Changelog entries in the **pull request title** are accurate, human-readable, and in the imperative mood.
<!-- Comment: TBD - release management
- [ ] Proper changelog labels are set so that the changelog can be generated automatically. See also [release-drafter-labels](https://github.com/jenkinsci/.github/blob/ce466227c534c42820a597cb8e9cac2f2334920a/.github/release-drafter.yml#L9-L50).
-->
- [ ] If the change needs additional upgrade steps from users, the `upgrade-guide-needed` label is set and there is a **Proposed upgrade guidelines** section in the pull request title (see [example](https://github.com/jenkinsci/jenkins/pull/4387)).
<!-- TBD
- [ ] Groovy code changes are tested by automated test.
-->
