# Jenkins lockable-resources-shared-library

Jenkins shared library to extends [lockable-resources-plugin](https://github.com/jenkinsci/lockable-resources-plugin) as a groovy code.

The goal is to extend lockable-resources-plugin functionality in simple way and provide a solution for end-user (administrators) with much more then one simple lock() step.

Many users has own solutions like create resources, or check it is isFree().
A lot of them are done in uncommon way and a lot of them make some useful magic.
This shall helps to all Jenkins administrators to interact with lockable-resource:

+ without coding own code
+ spare maintenance on own side
+ spare testing after each plugin and jenkins update
+ customizing
+ share ideas with community
+ do not copy examples from untrusted zones in to your code
+ be sure your solutions are done in proper way

---

## Usage

Install [Global Shared Library](https://jenkins.io/doc/book/pipeline/shared-libraries/) on our Jenkins instance.
Add this repository to your shared-libraries.
Enjoy in your pipelines.
<!-- TBD: describe detailed steps, and hallo world project-->

### Customizing

Fork this repository and change what you want.

> You can share your ideas with community, when you create pull-request into this repository. This wil help you to eliminate maintenance and help the community wit more power.

> Keep in mind, that we can not care about your changes. It means when this repository became an update, you need merge changes by your self.

---

## Changelog

See git history of *master* branch in this repository.

<!-- TBD: Describe release management here -->

---

## Report an Issue

Please report issues and enhancements through the [GitHub](https://github.com/mPokornyETM/jenkins-lockable-resources-shared-library/issues/new/choose).

If you have an example to share, please create a [new documentation issue](https://github.com/jenkinsci/lockable-resources-plugin/issues/new?assignees=&labels=documentation&template=3-documentation.yml) and provide additional examples as a [pull request](https://github.com/jenkinsci/lockable-resources-plugin/pulls) to the repository.

If you have a question, please open a [GitHub issue](https://github.com/jenkinsci/lockable-resources-plugin/issues/new/choose) with your question.

---

## Contributing

Contributions are welcome, please refer to the separate [CONTRIBUTING](CONTRIBUTING.md) document for details on how to proceed!

Join [Gitter channel](https://gitter.im/jenkinsci/lockable-resources) to discuss your ideas with the community.

---

## License

All source code is licensed under the MIT license.
See [LICENSE](LICENSE)
