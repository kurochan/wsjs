array = []
str = ""

while(str = gets)
  array.push str
end
array.reverse!

array.each do |str|
  puts str
end
