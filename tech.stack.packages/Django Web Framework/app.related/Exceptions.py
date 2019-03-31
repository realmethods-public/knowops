#======================================================================
# 
# Common Exception Handlers
#
# @author $aib.getAuthor()
#
#======================================================================

#======================================================================
# Class Exception - base exception class
#======================================================================

class GeneralError(Exception):

	type = "General Error"
	detail = ""

	def __init__(self, detail):
		self.detail = detail

	def __init__(self, type, detail):
		self.type = type
		self.detail = detail

class InputError(GeneralError):

	def __init__(self, detail):
		self.detail = detail
		self.type = "Input Error"

class StorageError(GeneralError):

	def __init__(self, detail):
		self.detail = detail
		self.type = "Storage Error"

class StorageReadError(StorageError):

	def __init__(self, detail):
		self.detail = detail
		self.type = "Storage Read Error"

class StorageWriteError(StorageError):

	def __init__(self, detail):
		self.detail = detail
		self.type = "Storage Write Error"

class ProcessingError(GeneralError):

	def __init__(self, detail):
		self.detail = detail
		self.type = "Processing Error"
		