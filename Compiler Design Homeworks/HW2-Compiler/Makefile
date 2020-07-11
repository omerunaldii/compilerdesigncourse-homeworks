#
# Filename:    Makefile
# Author:      Omer Faruk Ünaldı
# Dated:       April 17, 2019
# Description: Makefile for the parser
#

SHELL := /bin/sh
MAKE  := make
ROOT  := ./compiler

# Directories
INCLUDES := ./khudi
BIN_DIR  := ./bin

CC      := javac
RM      := rm
JAR     := jar
JFLAGS  := cmfv
CFLAGS  := -Xlint
LDFLAGS :=
LIBS    :=
.SUFFIXES: .java .class
MANIFEST_FILE := manifest.txt

echo_cmd = @echo   "  $(1)$(ECHO_FILE_NAME)" ; $(2)

MAKE := $(MAKE) --no-print-directory --silent
CC   := $(call echo_cmd,CC      ,$(CC))
LD   := $(call echo_cmd,LD      ,$(LD))
RM   := $(call echo_cmd,RM      ,$(RM))

usage:
	@echo ""
	@echo "Master Makefile for SUIS."
	@echo ""
	@echo "Usage:"
	@echo "	make all        # Compile everything"
	@echo "	make clean      # Remove all object files"
	@echo "	make clean_all  # Remove all object files and the binary"
	@echo ""

ERROR_DIR = $(ROOT)/Error
PARSER_DIR = $(ROOT)/Parsers
READER_DIR = $(ROOT)/Reader
SEMANTIC_DIR = $(ROOT)/Semantic
GENERATECODE_DIR = $(ROOT)/GenerateCode
VARIABLE_DIR = $(ROOT)/Variables
MAIN_DIR      = $(ROOT)

SRCS =	$(MAIN_DIR)/main.java \
		 $(VARIABLE_DIR)/Marker.java \
		 $(VARIABLE_DIR)/Register.java \
		 $(VARIABLE_DIR)/Variable.java \
         $(PARSER_DIR)/LineParser.java \
		 $(PARSER_DIR)/Parser.java \
		 $(READER_DIR)/ReadFile.java \
		 $(ERROR_DIR)/ErrorDetection.java \
		 $(ERROR_DIR)/ErrorCodes.java \
		 $(SEMANTIC_DIR)/SemanticAnalysis.java \
		 $(GENERATECODE_DIR)/CodeGenerator.java
all: $(SRCS) compiler.jar

OBJS = $(SRCS:.java=.class)

compiler.jar:  $(OBJS)
	$(JAR) $(JFLAGS) $(MANIFEST_FILE) compiler.jar $(OBJS)

.java.class:
	$(CC) $(CFLAGS) $<

clean:
	$(RM) $(OBJS)

clean_all:
	$(RM) $(OBJS) compiler.jar
