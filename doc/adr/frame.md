# Frame Package Design Guideline
## Prologue (Summary):

With the increasing use of environment modules, two issues have emerged regarding the placement of Java files:
The project has too many configuration-related packages, e.g., aspect, interceptor.
Some imported libraries are only used within a single model.

## Discussion (Context):

Define new rules for package placement.

## Solution:

Plan a frame package to organize Java files based on their scope of impact.
The frame package should be placed at the same level as the affected scope, making modular separation easier.

Consequences:

## Example Structure

com.project
├── frame
├── modelA
│    └── frame
│      └── aspect
│           └── CustomAspect.java
├── modelB
│ └── frame
│       └── interceptor
│               └── ModelBInterceptor.java

## Notes

- This is a convention, not a strict rule—use where it adds clarity.
- Avoid deep nesting under `frame`; keep contents focused and cohesive.