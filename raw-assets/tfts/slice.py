import argparse
import os
import re
from PIL import Image


parser = argparse.ArgumentParser(prog="slice")
parser.add_argument('input')
parser.add_argument('--output', default = 'textures')

comment_pattern = re.compile('^\s+#.+$')
region_pattern = re.compile('^([0-9]+)\s+([0-9]+)\s+(.+)$')


def read_region(regionPath):
	ret = []
	with open(regionPath) as f:
		for ln, line in enumerate(f.readlines(), 1):
			if comment_pattern.match(line):
				continue
			m = region_pattern.match(line)
			if m:
				print(m.group())
				ret.append(Region(int(m.group(1)), int(m.group(2)), m.group(3)))
			else:
				print('Invalid region format at line %s, skipping' % ln)
	return ret


class Region:
	def __init__(self, x, y, location):
		self.x = x
		self.y = y
		self.location = location

	def __str__(self):
		return '%s %s %s' % (self.x, self.y, self.location)
	def __repr__(self):
		return self.__str__()


args = parser.parse_args()

inputPath = os.path.abspath(args.input)
outputPath = os.path.abspath(args.output)

imagePath = inputPath + '.png'
regionPath = inputPath + '.slice'
if not os.path.exists(imagePath):
	print('Cannot find image file at %s' % imagePath)
	quit()
if not os.path.exists(regionPath):
	print('Cannot find region file at %s' % regionPath)
	quit()

regions = read_region(regionPath)
print('Read %s regions' % len(regions))

with Image.open(imagePath) as image:
	for r in regions:
		cropped = image.crop((r.x*16, r.y*16, r.x*16+16, r.y*16+16))
		outPath = '%s/%s.png' % (outputPath, r.location)
		os.makedirs(os.path.dirname(outPath), exist_ok = True)
		cropped.save(fp = outPath,
			format = 'png',
			optimize = True)