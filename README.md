# Terminal Buffer

### Tests and linter status:

[![codecov](https://codecov.io/gh/irinakomarchenko/terminal-buffer/branch/main/graph/badge.svg)](https://codecov.io/gh/irinakomarchenko/terminal-buffer)

![Java CI](https://github.com/irinakomarchenko/terminal-buffer/actions/workflows/ci.yml/badge.svg)

A Java implementation of the core terminal buffer data structure used by terminal emulators.

A terminal buffer keeps characters in a grid of cells, tracks the cursor position, and stores scrollback history for lines that moved off the visible screen.

The goal of this project is to implement the buffer logic independently of any UI layer.

---

## Project Overview

The buffer represents terminal output as a fixed-size screen backed by a scrollback history.

Each cell in the buffer contains:

- a character (or empty value)
- foreground color
- background color
- style flags (bold, italic, underline)

The buffer maintains a cursor position which determines where editing operations occur.

The buffer consists of two logical parts:

- **Screen**  -  the visible editable terminal area
- **Scrollback**  -  a history of lines that scrolled off the screen

---

## Architecture

The central class of the system is `TerminalBuffer`, which acts as a **facade** for the buffer subsystem.

It exposes the public API while delegating internal logic to specialized services.

Main components of the implementation:

### buffer
- `Screen` - represents the visible terminal grid
- `ScrollbackBuffer` - stores history lines

### model
- `Cell` - a single character cell with attributes
- `Line` - a fixed-width row of cells
- `Cursor` - current cursor position
- `TextAttributes` - foreground/background colors and styles
- `TerminalColor`
- `TextStyle`

### service
- `CursorService` - cursor positioning and movement
- `BufferWriteService` - overwrite text operations
- `BufferInsertService` - insert operations with shifting
- `BufferMaintenanceService` -  screen maintenance operations
- `BufferContentService` -  reading content from the buffer

The CLI demo interacts only with the `TerminalBuffer` facade, keeping the internal implementation encapsulated.

---

## Supported Operations

### Cursor

- set cursor position
- move cursor up, down, left and right
- cursor movement is clamped within screen bounds

### Editing

Editing operations use the current cursor position and attributes.

- **write(text)**  
  Writes text starting from the cursor position and overwrites existing content.

- **insert(text)**  
  Inserts text at the cursor position. Existing characters shift to the right and overflow may propagate to following lines.

- **fillLine(char)**  
  Fills the current line with the specified character.

### Maintenance

- **insertEmptyLineAtBottom()**  
  Moves the top screen line to scrollback and adds a new empty line at the bottom.

- **clearScreen()**  
  Clears the visible screen.

- **clearScreenAndScrollback()**  
  Clears both screen and scrollback history.

### Content Access

The buffer allows reading content from both screen and scrollback:

- `getCharacterAt(row, column)`
- `getAttributesAt(row, column)`
- `getLineAsString(row)`
- `getScreenContentAsString()`
- `getBufferContentAsString()`

Rows are interpreted relative to the logical buffer consisting of scrollback followed by screen.

---

## Build

This project uses **Maven**.

Compile the project:

```bash
mvn clean install
```

## Running Tests

The project contains unit tests covering:

- cursor behavior
- write operations
- insert operations
- maintenance operations
- content access


  The test suite includes boundary conditions and edge cases and ensures high code coverage.

Run tests with:
```bash
mvn test
```
## CLI Demo

A small CLI demo is included for manual interaction with the buffer.

Run the demo:
```bash
mvn exec:java -Dexec.mainClass="org.ikomarchenko.terminalbuffer.TerminalBufferCli"
```
## Example commands
```
write HelloWorld123
cursor 3 1
insert INSERT
print
clear
exit
```
## An example of the application's operation

[▶️  Watch a short demo of the terminal buffer in action](https://www.youtube.com/watch?v=ZrdzveyLhUc)