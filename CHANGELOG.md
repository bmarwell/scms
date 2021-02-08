# Changelog

## [v0.4.0](https://github.com/scms/scms/tree/v0.4.0) (2021-02-08)

[Full Changelog](https://github.com/scms/scms/compare/0.3.0...v0.4.0)

**Implemented enhancements:**

- Parallel processing [\#61](https://github.com/scms/scms/issues/61)
- Add version information from github [\#60](https://github.com/scms/scms/issues/60)
- Default output should show files being processed [\#59](https://github.com/scms/scms/issues/59)
- add asciidoc renderer [\#34](https://github.com/scms/scms/issues/34)
- extract api module, make renderer modular [\#27](https://github.com/scms/scms/issues/27)

**Fixed bugs:**

- Missing shutdown.await for executor service [\#64](https://github.com/scms/scms/issues/64)
- CI fails on windows [\#57](https://github.com/scms/scms/issues/57)
- Content not rendered [\#54](https://github.com/scms/scms/issues/54)
- IT uses Java 9's nullReader\(\). [\#52](https://github.com/scms/scms/issues/52)
- Templates must be resolved relative to the first parameter dir [\#3](https://github.com/scms/scms/issues/3)

**Closed issues:**

- Print all exceptions from threads [\#69](https://github.com/scms/scms/issues/69)
- Asciidoc CLI IT [\#51](https://github.com/scms/scms/issues/51)
- CLI IT [\#49](https://github.com/scms/scms/issues/49)
- easymock -\> mockito [\#48](https://github.com/scms/scms/issues/48)
- fix javadoc [\#46](https://github.com/scms/scms/issues/46)
- Show complete stacktrace on rendering error [\#2](https://github.com/scms/scms/issues/2)
- Update groovy dependencies [\#1](https://github.com/scms/scms/issues/1)

**Merged pull requests:**

- deploy to github. [\#73](https://github.com/scms/scms/pull/73) ([bmarwell](https://github.com/bmarwell))
- fix \#69: print errors for parallel processing. [\#71](https://github.com/scms/scms/pull/71) ([bmarwell](https://github.com/bmarwell))
- fix \#51: add CLI IT for Asciidoc source. [\#70](https://github.com/scms/scms/pull/70) ([bmarwell](https://github.com/bmarwell))
- fix \#60: version [\#66](https://github.com/scms/scms/pull/66) ([bmarwell](https://github.com/bmarwell))
- fix \#64: properly shutdown thread pool. [\#65](https://github.com/scms/scms/pull/65) ([bmarwell](https://github.com/bmarwell))
- fix \#59: default output and quiet option. [\#63](https://github.com/scms/scms/pull/63) ([bmarwell](https://github.com/bmarwell))
- fix \#61: parallel processing. [\#62](https://github.com/scms/scms/pull/62) ([bmarwell](https://github.com/bmarwell))
- fix \#57: fails on windows. [\#58](https://github.com/scms/scms/pull/58) ([bmarwell](https://github.com/bmarwell))
- fix \#48: remove easymock. [\#56](https://github.com/scms/scms/pull/56) ([bmarwell](https://github.com/bmarwell))
- fix \#54: read content fully for content rendering. [\#55](https://github.com/scms/scms/pull/55) ([bmarwell](https://github.com/bmarwell))
- fix \#52: nullReader from Java11 [\#53](https://github.com/scms/scms/pull/53) ([bmarwell](https://github.com/bmarwell))
- fix \#49: IT for CLI invocation. [\#50](https://github.com/scms/scms/pull/50) ([bmarwell](https://github.com/bmarwell))
- fix \#46: javadoc. [\#47](https://github.com/scms/scms/pull/47) ([bmarwell](https://github.com/bmarwell))
- fix \#34: asciidoc [\#45](https://github.com/scms/scms/pull/45) ([bmarwell](https://github.com/bmarwell))
- Update dependency org.apache.logging.log4j:log4j-to-slf4j to v2.14.0 [\#44](https://github.com/scms/scms/pull/44) ([renovate[bot]](https://github.com/apps/renovate))
- Cleanup [\#43](https://github.com/scms/scms/pull/43) ([bmarwell](https://github.com/bmarwell))
- Update dependency org.codehaus.mojo:appassembler-maven-plugin to v2 [\#36](https://github.com/scms/scms/pull/36) ([renovate[bot]](https://github.com/apps/renovate))
- fetch for spotless [\#33](https://github.com/scms/scms/pull/33) ([bmarwell](https://github.com/bmarwell))
- fix \#27: one module per renderer. [\#32](https://github.com/scms/scms/pull/32) ([bmarwell](https://github.com/bmarwell))
- Update dependency org.apache.maven.plugins:maven-compiler-plugin to v3 [\#31](https://github.com/scms/scms/pull/31) ([renovate[bot]](https://github.com/apps/renovate))
- Update dependency org.easymock:easymock to v3.6 [\#29](https://github.com/scms/scms/pull/29) ([renovate[bot]](https://github.com/apps/renovate))
- update velocity. [\#26](https://github.com/scms/scms/pull/26) ([bmarwell](https://github.com/bmarwell))
- Update dependency org.codehaus.gmavenplus:gmavenplus-plugin to v1.12.1 [\#25](https://github.com/scms/scms/pull/25) ([renovate[bot]](https://github.com/apps/renovate))
- asciidoc [\#23](https://github.com/scms/scms/pull/23) ([bmarwell](https://github.com/bmarwell))
- Update dependency commons-cli:commons-cli to v1.4 [\#19](https://github.com/scms/scms/pull/19) ([renovate[bot]](https://github.com/apps/renovate))
- begin junit jupiter conversion. [\#18](https://github.com/scms/scms/pull/18) ([bmarwell](https://github.com/bmarwell))
- pom plugin version updates. [\#17](https://github.com/scms/scms/pull/17) ([bmarwell](https://github.com/bmarwell))
- Update dependency com.diffplug.spotless:spotless-maven-plugin to v2.7.0 [\#16](https://github.com/scms/scms/pull/16) ([renovate[bot]](https://github.com/apps/renovate))
- Update dependency ch.qos.logback:logback-classic to v1.2.3 [\#15](https://github.com/scms/scms/pull/15) ([renovate[bot]](https://github.com/apps/renovate))
- Bump maven-assembly-plugin from 2.4 to 3.3.0 [\#11](https://github.com/scms/scms/pull/11) ([dependabot-preview[bot]](https://github.com/apps/dependabot-preview))
- Bump maven-gpg-plugin from 1.4 to 1.6 [\#10](https://github.com/scms/scms/pull/10) ([dependabot-preview[bot]](https://github.com/apps/dependabot-preview))

## [0.3.0](https://github.com/scms/scms/tree/0.3.0) (2016-10-19)

[Full Changelog](https://github.com/scms/scms/compare/0.2.0...0.3.0)

## [0.2.0](https://github.com/scms/scms/tree/0.2.0) (2016-10-18)

[Full Changelog](https://github.com/scms/scms/compare/0.1.2...0.2.0)

## [0.1.2](https://github.com/scms/scms/tree/0.1.2) (2013-04-21)

[Full Changelog](https://github.com/scms/scms/compare/0.1.1...0.1.2)

## [0.1.1](https://github.com/scms/scms/tree/0.1.1) (2013-04-18)

[Full Changelog](https://github.com/scms/scms/compare/scms-root-0.1.0...0.1.1)

## [scms-root-0.1.0](https://github.com/scms/scms/tree/scms-root-0.1.0) (2013-04-16)

[Full Changelog](https://github.com/scms/scms/compare/cfb9822c19804f2ff5061e7dd0eccc02ad1d495e...scms-root-0.1.0)



\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*
